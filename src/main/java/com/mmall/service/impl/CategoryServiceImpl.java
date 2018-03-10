package com.mmall.service.impl;
/**
 * SUCEESS IS NOT FINAL,FAILURE IS NOT FATAL.IT IS THE COURAGE TO CONTINUE THAT COUNTS
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 *@Author:yuantao
 *@Description:
 *@Date:Created in 14:57 2018/3/8
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(ICategoryService.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * @Desciption:添加品类
     * @Date: 20:53 2018/3/8
     */
    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    public ServerResponse<String> addCategory(HttpSession session,String categoryName,Integer parentId){
        //检查是否是管理员
        ServerResponse<String> validResponse = UserServiceImpl.checkAdminRole(session);
        if(validResponse.isSuccess()){
            Category category = new Category();
            if(categoryName!=null && StringUtils.isNotBlank(categoryName)){
                int resultCount;
                category.setName(categoryName);
                category.setParentId(parentId);
                category.setStatus(true);
                resultCount = categoryMapper.insertSelective(category);
                if(resultCount>0){
                    return ServerResponse.createBySuccessMessage("新建品类成功");
                }else {
                    return ServerResponse.createByErrorMessage("新建品类失败");
                }
            }else {
                return ServerResponse.createByErrorMessage("品类名称参数有误请重新输入");
            }
        }
        return validResponse;
    }
    /**
     * @Desciption:修改品类名称
     * @Date: 21:54 2018/3/8
     */
    public ServerResponse<String> updateCategoryName(HttpSession session,Integer categoryId,String categoryName){
        ServerResponse validResponse = UserServiceImpl.checkAdminRole(session);
        if(validResponse.isSuccess()){
            if(categoryId!=null && StringUtils.isNotBlank(categoryName)){
                Category category = new Category();
                category.setId(categoryId);
                category.setName(categoryName);
                int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
                if(resultCount>0){
                    return ServerResponse.createBySuccessMessage("修改品类名称成功");
                }else {
                    return ServerResponse.createByErrorMessage("修改品类失败");
                }
            }else {
                return ServerResponse.createByErrorMessage("品类参数有误请重新输入");
            }
        }else{
            return validResponse;
        }
    }
    /**
     * @Desciption:查询当前节点子品类平级的节点
     * @Date: 14:42 2018/3/9
     */
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session, Integer parentId){
        ServerResponse validResponse = UserServiceImpl.checkAdminRole(session);
        if(validResponse.isSuccess()){
            List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(parentId);
            if(CollectionUtils.isEmpty(categoryList)){
                logger.info("当前分类下未找到子分类");
                return ServerResponse.createByErrorMessage("当前分类下未找到子分类");
            }
            return ServerResponse.createBySuccess("查询子元素Category成功",categoryList);
        }else{
            return validResponse;
        }
    }
    /**
     * @Desciption:查询当前节点和递归子节点id
     * @Date: 14:43 2018/3/9
     */
    public ServerResponse<List<Integer>> getChildrenDeepCategory(HttpSession session,Integer categoryId){
        ServerResponse validResponse = UserServiceImpl.checkAdminRole(session);
        if(validResponse.isSuccess()){
            //TODO 为什么返回id而不是直接返回List<Category>? 在以后使用对象时要再次查询？
            //查询当前节点的id和递归子节点的id
             Set<Category> categorySet = Sets.newHashSet();
             if(categoryId != null){
                 this.findChildCategory(categorySet,categoryId);
                 if (CollectionUtils.isEmpty(categorySet)){
                     return ServerResponse.createByErrorMessage("找不到该ID对应的品类");
                 }

                 List<Integer> categoryList = Lists.newArrayList();
                 for (Category category:categorySet){
                     categoryList.add(category.getId());
                 }
                 //TODO
                 categoryList.sort(Integer::compareTo);
                 return ServerResponse.createBySuccess("查询当前节点和递归子节点成功",categoryList);
             }else{
                 return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"品类id不能为空");
             }
        }
        return validResponse;
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList){
            this.findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
