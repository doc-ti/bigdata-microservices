
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


mapsData = {}
arrayMaps = ["bssmap", "internalcause", "nrn", "operator", "ranap", "tac"]

for element in arrayMaps:
    print("--->" + element)
    mapaux = load_file_into_hashmap(element)
    mapsData[element] = mapaux

#print (mapsData)


