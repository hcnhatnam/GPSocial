/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ipcloation.constance.Const;
import com.iplocation.entites.IData;
import com.iplocation.service.IDataOperate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author namhcn
 * @param <T>
 */
public class GuavaCache<T extends IData> implements IDataOperate<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaCache.class);

    private final CacheLoader<String, Optional<T>> loader;
    private final LoadingCache<String, Optional<T>> cache;

    private IDataOperate<T> dataOperate;

    public GuavaCache(IDataOperate<T> dataOperate) {
        this.dataOperate = dataOperate;
        loader = new CacheLoader<String, Optional<T>>() {
            @Override
            public Optional<T> load(String id) {
                Optional<T> op = dataOperate.get(id);
                LOGGER.info("MISS_CACHE:" + Const.gson.toJson(op));
                return op;
            }
        };

        cache = CacheBuilder.newBuilder().maximumSize(1000).build(loader);
    }

    @Override
    public Optional<T> save(T obj) {
        cache.put(obj.getId(), Optional.of(obj));
        Optional<T> opObjDb = dataOperate.save(obj);
        if (opObjDb.isPresent()) {
            return opObjDb;
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> get(String _id) {
        try {
            Optional<T> value = cache.getUnchecked(_id);
            return value;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> getByField(String fieldName, String fieldValue) {
        try {
            List<T> listAll = getAllCache();
            for (T obj : listAll) {
                if (obj == null) { 
                    continue;
                }

                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value instanceof String) {
                    String getValueOffield = (String) value;
                    if (getValueOffield.equals(fieldValue)) {
                        return Optional.of(obj);
                    }
                } else {
                    throw new Exception("Field Type is not String");
                }
            }
            Optional<T> opObj = dataOperate.getByField(fieldName, fieldValue);
            cache.put(opObj.get().getId(), opObj);
            return opObj;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> update(T obj) {
        try {
            Optional<T> op = dataOperate.update(obj);
            if (op.isPresent()) {
                cache.refresh(obj.getId());
                return op;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> deleteUser(String id) {
        Optional<T> op = get(id);
        if (op.isPresent()) {
            cache.invalidate(op.get());
            return dataOperate.deleteUser(id);
        }
        return Optional.empty();
    }

    public List<T> getAllCache() {
        List<T> list = new ArrayList<>();
        ConcurrentMap<String, Optional<T>> cacheMap = cache.asMap();
        for (Map.Entry<String, Optional<T>> entry : cacheMap.entrySet()) {
            String key = entry.getKey();
            Optional<T> val = entry.getValue();
            if (val.isPresent()) {
                list.add(val.get());
            }
        }
        return list;
    }

}
