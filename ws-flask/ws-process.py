from flask import Flask, jsonify, request

# Create a Flask application
app = Flask(__name__)

mapsData = {}
arrayMaps = ["bssmap", "internalcause", "nrn", "operator", "ranap", "tac"]

fieldNames = [
	"cod_bssmap", 	"cod_internalcause",	"cod_nrn",	"cod_operator",	"cod_ranap",	"cod_tac",
	"field_3",	"field_4",		"field_5",	"field_6",
	"field_7",	"field_8",		"field_9",	"field_10",	"field_11",	"field_12",
	"field_13",	"field_14",		"field_15",	"field_16",	"field_17",	"field_18",
	"field_19",	"field_20",		"field_21",	"field_22",	"field_23",	"field_24",
	"field_25",	"field_26",		"field_27",	"field_28",	"field_29",	"field_30",
	"field_31",	"field_32",		"field_33",	"field_34",	"field_35",	"field_36",
	"field_37",	"field_38",		"field_39",	"field_40",	"field_41",	"field_42",
	"field_43",	"field_44",		"field_45",	"field_45",	"field_45",	"field_45",
	"field_49",	"field_50",		"field_51",	"field_52",	"field_53",	"field_54"
	]

IDENT='flask' 

@app.route('/identify', methods=['GET', 'POST'])
def identify():
    return IDENT


@app.route('/process', methods=['GET'])
def echo_get():
    data = request.args.get('record')
#    print ( data)

    myfields = data.split(',')

    out = {  }
#    out["len"] = len(myfields)
#    out["len2"] = len(fieldNames)

    if ( len(myfields) >= len (fieldNames) ) :
        for index in range(0, len (fieldNames)) :
           out[fieldNames[index]] = myfields[index]

        for index in range(0, 6) :
           htAux = mapsData[arrayMaps[index]]
#           out[arrayMaps[index]] = myfields[index]
           out[arrayMaps[index]] = htAux[myfields[index]]

    # Return the received data
    return jsonify(out)


# Define a route for '/api/echo' that accepts POST requests with parameters
@app.route('/process', methods=['POST'])
def echo_post():
    data = request.data.decode('utf-8').replace('record=', '')
#    print (data) 

    myfields = data.split(',')


    out = {  }
#    out["len"] = len(myfields)
#    out["len2"] = len(fieldNames)

    if ( len(myfields) >= len (fieldNames) ) :
        for index in range(0, len (fieldNames)) :
           out[fieldNames[index]] = myfields[index]

        for index in range(0, 6) :
           htAux = mapsData[arrayMaps[index]]
#           out[arrayMaps[index]] = myfields[index]
           out[arrayMaps[index]] = htAux[myfields[index]]

    # Return the received data
    return jsonify(out)



def load_file_into_hashmap(namemap):
    # Create an empty hashmap
    hashmap = {}
    print (namemap) 
    print(f"map_{namemap}.txt") 

    # Open the file and read its contents
    with open( f"master_{namemap}.txt", 'r') as file:
        # Iterate over each line in the file
        for line in file:
            parts = line.strip().split(';')

            if len(parts) >= 2:
                hashmap[parts[0]] = parts[1]

    return hashmap


contador = 0 

# Run the Flask application
if __name__ == '__main__':
    for element in arrayMaps:
        print("--->" + element)
        mapaux = load_file_into_hashmap(element)
        mapsData[element] = mapaux

    app.run(host='0.0.0.0', port=8080, debug=False)

