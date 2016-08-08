package com.asiawaters.fieldapprover;

import android.app.Application;

import com.asiawaters.fieldapprover.classes.DBController;
import com.asiawaters.fieldapprover.classes.Model_ListMembers;
import com.asiawaters.fieldapprover.classes.Model_NetState;
import com.asiawaters.fieldapprover.classes.Model_Person;
import com.asiawaters.fieldapprover.classes.NetListener;

import java.util.Date;

public class FieldApprover extends Application {
    private Model_Person person;
    private Model_ListMembers listMember;
    private NetListener mnetListener = new NetListener();
    private Model_NetState model_netState = new Model_NetState();
    private Model_ListMembers[] list_values;
    private String path_url = "http://ws.asiawaters.com/ast2/ws/Mobile";
    private DBController db;
    private int timeOut = 60000;
    private int idGroup = -1;
    private int idPosition = -1;
    private Date DateFrom;
    private Date DateTo;
    private Boolean UpdateList = false;
    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUpdateList() {
        return UpdateList;
    }

    public void setUpdateList(Boolean updateList) {
        UpdateList = updateList;
    }

    public Date getDateFrom() {
        return DateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        DateFrom = dateFrom;
    }

    public Date getDateTo() {
        return DateTo;
    }

    public void setDateTo(Date dateTo) {
        DateTo = dateTo;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public int getIdPosition() {
        return idPosition;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }

    public DBController getDb() {
        return db;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setDb(DBController db) {
        this.db = db;
    }

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

    public String getPath_url() {
        return path_url;
    }

    public void setPath_url(String path_url) {
        this.path_url = path_url;
    }

    public Model_ListMembers[] getList_values() {
        return list_values;
    }

    public void setList_values(Model_ListMembers[] list_values) {
        this.list_values = list_values;
    }
}
