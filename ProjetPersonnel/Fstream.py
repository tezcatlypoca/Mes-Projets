import json, os

# Class use to read/write on json file easily
class Fstream:

    # create a new json file w/a specific path and basic structure inside
    def create(self, path):
        try:
            with open(path, 'w') as file:
                file.write(json.dumps({"crypto": [], "exchange": [], "triples": []}))
        except json.decoder.JSONDecodeError as e:
            return e
        finally:
            print(f"File {path.split('.')[0]} created: OK")
            return True
    # end function create

    # read and return the file as table - dictio
    # params:
    #       -> path = str
    # return:
    #       -> dictio
    def read(self, path):
        if not os.path.exists(path):
            print({"code": "-1201", "msg": "ERROR: File not found."})
            return False

        if os.path.getsize(path) == 0:
            print({"code": "-1202", "msg": "ERROR: File empty."})
            return False 
        
        with open(path, 'r') as file:
            return json.load(file)
    # end read

    # read a specific key and return the file as table - dictio
    # params:
    #       -> path = str
    #       -> key = str
    # return:
    #       -> dictio
    def read_key(self, path, keyname):
        file = self.read(path)

        if keyname in file:
            return file[keyname]
        else:
            print({"code": "-1203", "msg": "ERROR: key not found."})
            return False
    # end read_key

    # return triples who start with a crypto own in the spot account
    # params:
    #       ->spot_account: tab[tuple(str, float)]
    def get_triples(self, spot):
        file = self.read_key("data_parameters.json", "triples")
        temp = []

        for triple in file:
            for crypto in spot:
                if triple['symbol'][0] == crypto[0]:
                    temp.append(triple)
        return temp

    # end get_triples

    # search info in specific field and return true if
    # params:
    #       -> path: str
    #       -> keyname: str
    #       -> search: str
    def search(self, path, keyname, search):
        file = self.read(path)

        if keyname in file:
            for item in file[keyname]:
                if item['symbol'] == search:
                    return True
            else: 
                return False
    # end function search

    # write some data on path's file even if the file doesn't exist
    # params:
    #       -> path = str
    #       -> dataname = str - name for the key on json file
    #       -> data = [] or {}
    # return:
    #       -> message for error or well done task
    def write(self, path, Keyname, data):        
        try:
            with open(path, 'w') as f:
                f.write(json.dumps({Keyname: data}))
        except json.decoder.JSONDecodeError as e:
            return e
        finally:
            print(f"Write '{Keyname}' on {path.split('.')[0]}: OK")
            return True
    # end write

    # write some data on path's file 
    # params:
    #       -> path = str
    #       -> dataname = str - name for the key on json file
    #       -> data = [] or {}
    # return:
    #       -> message for error or well done task
    def append(self, path, Keyname, data):
        if not os.path.exists(path):
            self.create(path)
        
        file = self.read(path)
        file[Keyname] = data

        try:
            with open(path, 'w') as f:
                f.write(json.dumps(file))
        except json.decoder.JSONDecodeError as e:
            return e
        finally:
            print(f"Write '{Keyname}' on {path.split('.')[0]}: OK")
            return True
    # end append

    # update data in json file 
    # params:
    #       -> path = str
    #       -> Keyname = str (only existing)
    #       -> data = [] or {}
    # return:
    #       -> error code
    def update(self, path, keyname, data):
        file = self.read(path)

        # testing if the key given exists in the dict file
        if not keyname in file:
            print({"code": "-1203", "msg": "ERROR: key not found."})
            return False
        
        file[keyname] = data

        self.write(path, keyname, data)
    # end update

    # delete data and his keyname from json file
    # params:
    #       -> path = str
    #       -> keyname
    # return:
    #       -> error code
    def delete(self, path, keyname):
        file =  self.read(path)

        del file[keyname]
        
        try:
            with open(path, 'w') as f:
                f.write(json.dumps(file))
        except:
            print({"code": "-1204", "msg": "ERROR: cannot delete data on file."})
            return False
        finally:
            print("Data successfull deleted.")
            return True
    # end delete

# END CLASS FSTREAM        


## DEBUG ##