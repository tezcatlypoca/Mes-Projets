import winsound, time
import pandas as pd
import sqlalchemy as sql
from Fstream import Fstream
from binance import Client
from threading import Thread


class BillyBot(Thread):

    API_KEY = "YOUR_API_KEY"
    SECRET_KEY = "YOUR_SECRET_KEY"

    lock = None # object lock shared by class Pricer from Factory file
    client = None # object Client from Binance modul
    engine = None # object use to request data from bdd
    spot_account = [] # table containing all cryptos on your binance spot account
    triples = [] # list of triples build on class Builder from Factory file
    is_running = False # bool to stop the while true
    
    # constructor
    def __init__(self, lock):
        Thread.__init__(self, daemon=True) 
        self.client = Client(self.API_KEY, self.SECRET_KEY) # initiate object client
        self.engine = sql.create_engine('sqlite:///priceStream.db') # initiating object engine
        self.init_spot_account() # initiating list of cryptos
        self.lock = lock # instiating lock object
        self.is_running = True
    # end constructor

    # basic function for thread class work
    def run(self):
        while self.is_running:
            self.lock.acquire(blocking=True)

            for item in Fstream().read_key('data.json', 'triples'):
                # fetch all prices from bdd
                price1 = self.get_price(f"{item['symbol'][0]}{item['symbol'][1]}")
                price2 = self.get_price(f"{item['symbol'][1]}{item['symbol'][2]}")
                price3 = self.get_price(f"{item['symbol'][2]}{item['symbol'][0]}")

                # fetch and calculating base amount of entering crypto's triple and yield of the current triple
                base_amount = self.get_base_amount(item['symbol'][0])
                yield_ = ((base_amount * price1) * price2) * price3

                # make sound if triple get capital gain
                if yield_ > base_amount:
                    print(f"{item['symbol']} | Entrée: {base_amount} | Sortie: {yield_} => Gagnant (sans frais)")
                    winsound.Beep(250, 1000)
            
            self.lock.release()
            time.sleep(0.5)
    # end function test_triple

    # fetch all crypto on my account higher than min notional
    def init_spot_account(self):
        # fetch all data from your binance spot account
        data = self.client.get_account()['balances']
        for item in data:
            # filter only cryptos which amount superior 0
            if float(item['free']) != 0.0:
                self.spot_account.append((item['asset'], item['free']))
    # end function init_spot_account

    # fetch triples built on json file
    def init_triples(self):
        for item in Fstream().read_key('data.json', 'triples'):
            self.triples.append(item)
    # end function init_triples

    # fetch price on database about the peer symbol in param
    def get_price(self, peer):
        try:
            # try to fetch price from bdd
            df = pd.read_sql(f"{peer}", self.engine)
            return float(df.iloc[-1]['Price'])
        except:
            return 0.0
    # end function get_price

    # pull the amount of the first crypto triple on spot account
    def get_base_amount(self, crypto):
        # run throught a list of crypto's amount
        for item in self.spot_account:
            if item[0] == crypto:
                return float(item[1])
            else:
                return 0.0
    # end function get_base_amount

# end class BillyBot

## DEBUG ##
# bb = BillyBot()
# while True:
#     try:
#         bb.test_triple()
#     except KeyboardInterrupt:
#         pass
