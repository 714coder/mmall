package com.mmall.controller.backend;
/**
 * SUCEESS IS NOT FINAL,FAILURE IS NOT FATAL.IT IS THE COURAGE TO CONTINUE THAT COUNTS
 */

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 *@Author:yuantao
 *@Description:
 *@Date:Created in 16:38 2018/3/9
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("save_product.do")
    @ResponseBody
    public ServerResponse<String> saveProduct(HttpSession session,Product product){
        return iProductService.saveOrUpdateProduct(session,product);
    }
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpSession session,Product product){
        return iProductService.setSaleStatus(session,product);
    }

    @RequestMapping("manage_product_detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> manageProductDetail(HttpSession session, Integer productId){
        return iProductService.manageProductDetail(session,productId);
    }
    @RequestMapping("get_product_list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return iProductService.getProductList(session,pageNum,pageSize);
    }
}
