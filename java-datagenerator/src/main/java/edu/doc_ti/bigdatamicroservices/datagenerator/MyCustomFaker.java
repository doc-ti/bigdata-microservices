package edu.doc_ti.bigdatamicroservices.datagenerator;

import net.datafaker.providers.base.BaseFaker;

public class MyCustomFaker extends BaseFaker {

    public MyElements MyElements() {
        return getProvider(MyElements.class, MyElements::new, this);
    }


    
}

