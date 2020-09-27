/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author namhcn
 * @param <T>
 */
public interface IDataOperate<T> {

    public Optional<T> save(T userFireBase);

    public Optional<T> get(String _id);

    public Optional<T> getByField(String fieldName, String fieldValue);

    public Optional<T> update(T obj);

    public Optional<T> deleteUser(String id);

}
