package com.mmall.service.impl;
/**
 * SUCEESS IS NOT FINAL,FAILURE IS NOT FATAL.IT IS THE COURAGE TO CONTINUE THAT COUNTS
 */

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 *@Author:yuantao
 *@Description:
 *@Date:Created in 16:40 2018/3/9
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse<String> saveOrUpdateProduct(HttpSession session,Product product){
        ServerResponse validResponse = UserServiceImpl.checkAdminRole(session);
        if(validResponse.isSuccess()){
            if(product != null){
                this.setMainImages(product);
                if(product.getId()!=null){
                    //判断是更新操作
                    int resultCount = productMapper.updateByPrimaryKeySelective(product);
                    if(resultCount>0){
                        return ServerResponse.createBySuccessMessage("更新产品成功");
                    }else{
                        return ServerResponse.createByErrorMessage("更新产品失败");
                    }
                }else{
                    //判断是保存操作
                    int resultCount = productMapper.insert(product);
                    if(resultCount>0){
                        return ServerResponse.createBySuccessMessage("新增产品成功");
                    }else{
                        return ServerResponse.createByErrorMessage("新增产品失败");
                    }
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"商品不能是空");
        }
        return validResponse;
    }

    /**
     * @Desciption:更新产品的status
     * @Date: 23:59 2018/3/9
     */
    @Override
    public ServerResponse<String> setSaleStatus(HttpSession session, Product product) {
        ServerResponse validResponse = UserServiceImpl.checkAdminRole(session);
        if(validResponse.isSuccess()){
            return updateProductByProductId(product, Const.STATUS);
        }
        return validResponse;
    }

    /**
     * @Desciption:后台获取商品信息
     * @Date: 0:16 2018/3/10
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(HttpSession session,Integer productId){
        ServerResponse validResponse = UserServiceImpl.checkAdminRole(session);
        if(validResponse.isSuccess()){
            if(productId == null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product == null){
                return ServerResponse.createByErrorMessage("产品已下架或者删除");
            }
            ProductDetailVo productDetailVo = this.assembleProductDetailVo(product);
            return ServerResponse.createBySuccess("后台获取商品详情成功",productDetailVo);
        }
        return validResponse;
    }

    /**
     * @Desciption:获取数据库中的所有商品分页显示商品列表
     * @Date: 11:11 2018/3/10
     */
    public ServerResponse<PageInfo> getProductList(HttpSession session, Integer pageNum, Integer pageSize){
        ServerResponse validResponse = UserServiceImpl.checkAdminRole(session);
        if(validResponse.isSuccess()){
            PageHelper.startPage(pageNum,pageSize);
            List<Product> productList = productMapper.selectList();
            if(!productList.isEmpty()){
                //分页查询产品成功
                List<ProductListVo> productListVoList = Lists.newArrayList();
                for (Product productItem:productList){
                    ProductListVo productListVo = new ProductListVo(productItem);
                    productListVoList.add(productListVo);
                }
                PageInfo pageResult = new PageInfo(productList);
                pageResult.setList(productListVoList);
                return ServerResponse.createBySuccess("分页查询成功",pageResult);
            }else{
                return ServerResponse.createByErrorMessage("未找到产品，分页查询失败");
            }
        }
        return validResponse;
    }

    /**
     * @Desciption:取从图第一个图片作为主图
     * @Date: 9:26 2018/3/10
     */
    private void setMainImages(Product product){
        String subImage = product.getSubImages();
        if(subImage != null){
            String[] subImagesArray = subImage.split(",");
            product.setMainImage(subImagesArray[0]);
        }
        logger.info("产品从图为空，不能截取主图");
    }
    /**
     * @Desciption:高复用的更新方法
     * @Date: 23:59 2018/3/9
     */
    private ServerResponse<String> updateProductByProductId(Product product,String type){
        if(Const.STATUS==type){
            //更新产品status
            if(product.getId()!=null && product.getStatus()!=null){
                int resultCount = productMapper.updateStatusByProductId(product.getStatus(),product.getId());
                if(resultCount>0){
                    return ServerResponse.createBySuccessMessage("更新产品status成功");
                }
            }
            else{
                return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
        }
        return ServerResponse.createByErrorMessage("找不到要操作的类型");
    }
    /**
     * @Desciption:组装productDetailVo对象
     * @Date: 0:18 2018/3/10
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo(product);

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category != null){
            //默认根节点
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getId());
        }
        // joda-time 设置标准格式时间
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime(),DateTimeUtil.STANDARD_FORMAT));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime(),DateTimeUtil.STANDARD_FORMAT));
        return productDetailVo;
    }
}
