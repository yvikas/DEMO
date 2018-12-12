package com.mangal.demo.model;

import java.util.List;

public class MyResponse {

    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_id;
    public List<Result> results;
}
