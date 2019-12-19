package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;

import java.util.List;

/**
 * @Author: nullWagesException
 * @Date: 2019/12/19 15:29
 * @Description:
 */
public interface BrandService {

    /***
     * 查询所有品牌
     * @return
     */
    List<Brand> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Brand findById(Integer id);

    /***
     * 新增品牌
     * @param brand
     */
    void add(Brand brand);

    /***
     * 修改品牌数据
     * @param brand
     */
    void update(Brand brand);

    /***
     * 删除品牌
     * @param id
     */
    void delete(Integer id);
}
