package com.asiawaters.fieldapprover;
import android.app.Application;

import com.asiawaters.fieldapprover.classes.Model_ListMembers;
import com.asiawaters.fieldapprover.classes.Model_Person;

public class FieldApprover extends Application {
    private Model_Person person;
    private Model_ListMembers listMember;

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
}
