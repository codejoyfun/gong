package com.runwise.supply.entity;

import java.util.List;

/**
 * Created by myChaoFile on 17/1/18.
 */

public class SelectOptionList {
    private List<SelectOption> banks;
    private List<SelectOption> works;
    private List<SelectOption> jobs;
    private List<SelectOption> marrys;
    private List<SelectOption> relations;

    public List<SelectOption> getJobs() {
        return jobs;
    }

    public void setJobs(List<SelectOption> jobs) {
        this.jobs = jobs;
    }

    public List<SelectOption> getRelations() {
        return relations;
    }

    public void setRelations(List<SelectOption> relations) {
        this.relations = relations;
    }

    public List<SelectOption> getBanks() {
        return banks;
    }

    public void setBanks(List<SelectOption> banks) {
        this.banks = banks;
    }

    public List<SelectOption> getWorks() {
        return works;
    }

    public void setWorks(List<SelectOption> works) {
        this.works = works;
    }

    public List<SelectOption> getMarrys() {
        return marrys;
    }

    public void setMarrys(List<SelectOption> marrys) {
        this.marrys = marrys;
    }
}
