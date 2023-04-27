import requests, json, sqlalchemy, time
import pandas as pd
from websocket import WebSocketApp
from datetime import datetime
from Fstream import Fstream
from threading import Thread
from requests.exceptions import ConnectionError


# fetch crypto from coin market cap
class Cmc:

    #### global variables ####
    # for cmc data
    url = None
    parameters = None
    headers = None
    session = None

    #for exchange data
    exchange_data = None

    # default constructor
    def __init__(self):
        self.url = 'https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest'
        self.parameters = {
        'start':'1',
        'limit':'15',
        'convert':'USD'
        }
        self.headers = {
        'Accepts': 'application/json',
        'X-CMC_PRO_API_KEY': '52de92e6-01c1-4e69-86cd-f1ec88a6d985',
        }

        self.session = requests.Session()
        self.session.headers.update(self.headers)
    # fin constructor

    # get top 15 of cryptos momentum on Coin Market Cap API
    # and write it on json file
    def get_data(self):
        try:
            # try to requests 15 first cryptos listed on coinmarketcap
            response = self.session.get(self.url, params=self.parameters)
            # convert to json data
            data = json.loads(response.text)
            temp =[]
            for item in data['data']:
                temp.append(item['symbol'])
            
            # date = f"{datetime.now().day}/{datetime.now().month}/{datetime.now().year}"
            return self.parsing(temp)
        except ConnectionError as e:
            print(e)
    # end get_data

    #delete stablecoins and bnb coin from list used
    # to build peers
    def parsing(self, table):
        temp = []

        for item in table:
            if 'USD' not in item and 'BNB' not in item and 'DOGE' not in item and 'SHIB' not in item:
                temp.append(item)
        # write on json file
        Fstream().append("data.json", "crypto", temp)
    # end function parsing

## END CLASS CMC

# fetch all info about each peer listed on binance exchange
class Bnb:

    # fetch all data about peers 
    # from url binance api
    # return:
    #       -> list() -> 2000 items
    def get_data(self):
        data = requests.get("https://api.binance.com/api/v3/exchangeInfo").json()['symbols']
        return data
    #end function get_data

    # filter peer usefull for my bot
    # write them in json file with keyname 'exchange'
    def filter(self):
        all_crypto = self.get_data()
        crypto_allows = Fstream().read_key('data.json', 'crypto')
        temp = []
        
        for item in all_crypto:
            # verify if a peer match with crypto allowed for build peers
            if item['baseAsset'] in crypto_allows and item['quoteAsset'] in crypto_allows:
                temp.append(item)
            if (item['baseAsset'] in crypto_allows or item['quoteAsset'] in crypto_allows) and 'BNB' in item['symbol']:
                temp.append(item)
        Fstream().append("data.json", "exchange", temp)
    # end function filter

# END CLASS BNB

# build all ressources needed to take trade
# peers and triples
class Builder:

    peers = [] # list of dictio

    # build all peers possible and their reverses
    def build_peers(self):
        data = Fstream().read_key('data.json', 'exchange')
        
        for item in data:
            if 'BNB' not in item['symbol']:
                # basic peer
                self.peers.append(
                    {
                        'symbol': (item['baseAsset'], item['quoteAsset']),
                        'minQty': item['filters'][1]['minQty'],
                        'stepSize': item['filters'][1]['stepSize'],
                        'minNotional': item['filters'][2]['minNotional'],
                        'isSelling': True
                    }
                )
                # reverse peer
                self.peers.append(
                    {
                        'symbol': (item['quoteAsset'], item['baseAsset']),
                        'minQty': item['filters'][1]['minQty'],
                        'stepSize': item['filters'][1]['stepSize'],
                        'minNotional': item['filters'][2]['minNotional'],
                        'isSelling': False
                    }
                )
    # end function build_peers

    # build triples with bnb peers needed to pay fees        
    def build_triples(self):
        temp = []

        for item in self.peers:
            for jtem in self.peers:
                    if item['symbol'] != jtem['symbol']:
                        for ktem in self.peers:
                            if item['symbol'][1] == jtem['symbol'][0] and jtem['symbol'][1] == ktem['symbol'][0] and ktem['symbol'][1] == item['symbol'][0]:
                                res = self.find_bnb_peer([item['symbol'][0], jtem['symbol'][0], ktem['symbol'][0]])
                                temp.append({
                                        "symbol": [item['symbol'][0], jtem['symbol'][0], ktem['symbol'][0]],
                                        "peerFees": res
                                    })
        Fstream().append('data.json', 'triples', temp)
    # end function build_triples

    # find the right peer with bnb inside
    # to pay fees
    def find_bnb_peer(self, triple):
        table_bnb = []
        for xtem in Fstream().read_key('data.json', 'exchange'):
            if f"BNB{triple[0]}" == xtem['symbol'] or f"{triple[0]}BNB" == xtem['symbol']:
                table_bnb.append(xtem['symbol'])
            if f"BNB{triple[1]}" == xtem['symbol'] or f"{triple[1]}BNB" == xtem['symbol']:
                table_bnb.append(xtem['symbol'])
            if f"BNB{triple[2]}" == xtem['symbol'] or f"{triple[2]}BNB" == xtem['symbol']:
                table_bnb.append(xtem['symbol'])
        return table_bnb
    # end function find_bnb_peer

# END CLASS BUILDER

class Pricer(Thread):

    lock = None # object lock 
    ws = None # object websocket
    engine = None # object use for request bdd
    peers = []
    is_running = False

    # constructor override
    def __init__(self, lock):
        # basic initiation for inherit class thread
        Thread.__init__(self, daemon=True)
        self.lock = lock
        self.is_running = True
        self.init_peers() # read peers from json file and store them on list
        link = f"wss://stream.binance.com:9443/ws/{'/'.join(self.peers)}"
        # create connexion with bdd
        self.engine = sqlalchemy.create_engine('sqlite:///priceStream.db')
        # instantiating websocket object with parameters 
        self.ws = WebSocketApp(link, on_open=self.on_open, on_message=self.on_message, on_close=self.on_close)
    # end constructor

    # basic function for thread class work
    def run(self):
        self.ws.run_forever()

    def on_open(self, ws):
        print('Connected successfully...')
    # end function on_open

    def on_message(self, ws, message):
        if not self.is_running:
            self.on_close(ws)
        self.lock.acquire(blocking=True)
        # print('Fetch some price')
        json_msg = json.loads(message)
        frame = self.create_frame(json_msg)
        frame.to_sql(f"{json_msg['s']}", self.engine, if_exists='append')
        # print("End work Pricer")
        self.lock.release()
        time.sleep(0.5)
        # print(frame)
    # end function on_message
    
    def on_close(self, ws):
        ws.close()
        print('Closed connexion...')
    # end function on_close

    # read peer tradable on binance
    # get there symbols to fetch price next                                                                                                                                                                                                                                                                                                                                                                   
    def init_peers(self):
        for item in Fstream().read_key('data.json', 'exchange'):
            self.peers.append(f"{item['symbol'].lower()}@miniTicker")
    # end function init_peers

    # create pandas frame to store price from ws
    def create_frame(self, msg):
        df = pd.DataFrame([msg])
        df = df.loc[:,['s', 'c']]
        df.columns = ['Symbol', 'Price']
        df.Price = df.Price.astype(float)
        return df
    # end function create_frame

# END CLASS PRICER

## DEBUG ##
# cmc = Cmc()
# cmc_data = cmc.get_data()
# bnb = Bnb()
# res = bnb.filter()
# bld = Builder()
# bld.build_peers()
# bld.build_triples()
# pr = Pricer()

## To read data on database sqlalchemy ##
# engine = sqlalchemy.create_engine('sqlite:///priceStream.db')
# try:
#     df = pd.read_sql('ETHBTC', engine)
#     print(df)
# except:
#     print('Table unknown')
