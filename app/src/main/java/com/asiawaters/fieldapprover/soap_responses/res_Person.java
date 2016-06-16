package com.asiawaters.fieldapprover.soap_responses;

import java.util.Vector;
import java.util.Date;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import com.asiawaters.fieldapprover.classes.Model_Person;

public class res_Person implements KvmSerializable {

    public String NAMESPACE = "http://Wsdl2CodeTestService";
    public Model_Person customArray;
    public String errMessage;
    public int resultCode;

    public res_Person() {
    }

    public res_Person(SoapObject soapObject) {

        if (soapObject.hasProperty("CustomArray")) {
            SoapObject j10 = (SoapObject) soapObject.getProperty("CustomArray");
            customArray = new Model_Person();
        }
        if (soapObject.hasProperty("stringArray")) {
            SoapObject j11 = (SoapObject) soapObject.getProperty("stringArray");
        }
        if (soapObject.hasProperty("errMessage")) {
            Object obj = soapObject.getProperty("errMessage");
            if (obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j12 = (SoapPrimitive) soapObject.getProperty("errMessage");
                errMessage = j12.toString();
            }
        }
        if (soapObject.hasProperty("resultCode")) {
            Object obj = soapObject.getProperty("resultCode");
            if (obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j13 = (SoapPrimitive) soapObject.getProperty("resultCode");
                resultCode = Integer.parseInt(j13.toString());
            }
        }
    }

    @Override
    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return customArray;
            case 1:

            case 2:
                return errMessage;
            case 3:
                return resultCode;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 4;
    }

    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.VECTOR_CLASS;
                info.name = "CustomArray";
                break;
            case 1:
                info.type = PropertyInfo.VECTOR_CLASS;
                info.name = "stringArray";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "errMessage";
                break;
            case 3:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "resultCode";
                break;
        }
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                customArray = (Model_Person) value;
                break;
            case 1:

                break;
            case 2:
                errMessage = value.toString();
                break;
            case 3:
                resultCode = Integer.parseInt(value.toString());
                break;
        }
    }
}

