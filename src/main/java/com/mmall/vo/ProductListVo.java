package com.mmall.vo;
/**
 * SUCEESS IS NOT FINAL,FAILURE IS NOT FATAL.IT IS THE COURAGE TO CONTINUE THAT COUNTS
 */

import com.mmall.pojo.Product;
import com.mmall.util.PropertiesUtil;

import java.math.BigDecimal;

/**
 *@Author:yuantao
 *@Description:
 *@Date:Created in 11:46 2018/3/10
 */
public class ProductListVo {

    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private String imageHost;

    public ProductListVo(Product product) {
        this.id = product.getId();
        this.categoryId = product.getCategoryId();
        this.name = product.getName();
        this.subtitle = product.getSubtitle();
        this.mainImage = product.getMainImage();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.status = product.getStatus();
        this.imageHost = PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
