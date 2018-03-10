package com.mmall.service;
/**
 * SUCEESS IS NOT FINAL,FAILURE IS NOT FATAL.IT IS THE COURAGE TO CONTINUE THAT COUNTS
 */

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 *@Author:yuantao
 *@Description:
 *@Date:Created in 16:39 2018/3/9
 */
public interface IProductService {

    ServerResponse<String> saveOrUpdateProduct(HttpSession session, Product product);

    ServerResponse<String> setSaleStatus(HttpSession session,Product product);

    ServerResponse<ProductDetailVo> manageProductDetail(HttpSession session, Integer productId);

    ServerResponse<PageInfo> getProductList(HttpSession session, Integer pageNum, Integer pageSize);
}
