package com.asiawaters.fieldapprover;
import android.app.Application;

import com.asiawaters.fieldapprover.classes.Model_ListMembers;
import com.asiawaters.fieldapprover.classes.Model_NetState;
import com.asiawaters.fieldapprover.classes.Model_Person;
import com.asiawaters.fieldapprover.classes.NetListener;

public class FieldApprover extends Application {
    private Model_Person person;
    private Model_ListMembers listMember;
    private NetListener mnetListener = new NetListener();
    private Model_NetState model_netState = new Model_NetState();
    private Model_ListMembers[] list_values;

    public Model_ListMembers getListMember() {
        return listMember;
    }

    public void setListMembers(Model_ListMembers listMember) {
        this.listMember = listMember;
    }

    public Model_Person getPerson() {
        return person;
    }
    public void setPerson(Model_Person person) {
        this.person = person;
    }

    public NetListener getMnetListener() {
        return mnetListener;
    }

    public void setMnetListener(NetListener mnetListener) {
        this.mnetListener = mnetListener;
    }

    public Model_NetState getModel_netState() {
        return model_netState;
    }

    public void setModel_netState(Model_NetState model_netState) {
        this.model_netState = model_netState;
    }

    public Model_ListMembers[] getList_values() {
        return list_values;
    }

    public void setList_values(Model_ListMembers[] list_values) {
        this.list_values = list_values;
    }
}
