package edu.doc_ti.bigdatamicroservices.ws_spring.resource;

import org.json.JSONException;
import org.json.JSONObject;


public class DataProcessing {

	String fieldNames[] = {
			"cod_bssmap", 		"cod_internalcause",	"cod_nrn",		"cod_operator",		"cod_ranap",			"cod_tac",
			"field_3",			"field_4",			"field_5",			"field_6",
			"field_7",			"field_8",			"field_9",			"field_10",			"field_11",			"field_12",
			"field_13",			"field_14",			"field_15",			"field_16",			"field_17",			"field_18",
			"field_19",			"field_20",			"field_21",			"field_22",			"field_23",			"field_24",
			"field_25",			"field_26",			"field_27",			"field_28",			"field_29",			"field_30",
			"field_31",			"field_32",			"field_33",			"field_34",			"field_35",			"field_36",
			"field_37",			"field_38",			"field_39",			"field_40",			"field_41",			"field_42",
			"field_43",			"field_44",			"field_45",			"field_45",			"field_45",			"field_45",
			"field_49",			"field_50",			"field_51",			"field_52",			"field_53",			"field_54"
	};
	
	public String process(String input) {
		
		String inputMod = input ;
		if ( inputMod.startsWith("record=") ) {
			inputMod = input.substring(7) ;
		}
		if ( inputMod.startsWith("data=") ) {
			inputMod = input.substring(5) ;
		}
		
		JSONObject json= new JSONObject();

		String fields[] = inputMod.split(",") ;
		
		if ( fields.length == fieldNames.length ) {
			for ( int pos = 0 ; pos < fields.length ; pos++) {
				try {
					json.put(fieldNames[pos], fields[pos]) ;
				} catch (JSONException e) {}
			}
		}
		
		if ( fields.length >= LookupData.namesHT.length ) {
			for ( int pos = 0 ; pos < LookupData.namesHT.length ; pos++) {
				String name = LookupData.namesHT[pos] ;
				try {
					json.put(name, LookupData.htMain.get(name).get(fields[pos])) ;
				} catch (JSONException e) {}
			}
		}
		
		return json.toString() ;
	}
	
	
}
