package com.mmall.controller.backend;
/**
 * SUCEESS IS NOT FINAL,FAILURE IS NOT FATAL.IT IS THE COURAGE TO CONTINUE THAT COUNTS
 */

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 *@Author:yuantao
 *@Description:商品分类Controller
 *@Date:Created in 14:30 2018/3/8
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManagerController {

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        return iCategoryService.addCategory(session,categoryName,parentId);
    }

    @RequestMapping("update_category_name.do")
    @ResponseBody
    public ServerResponse<String> updateCategoryName(HttpSession session,String categoryName,Integer categoryId){
        return iCategoryService.updateCategoryName(session,categoryId,categoryName);
    }

    @RequestMapping("get_parallel_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        return iCategoryService.getChildrenParallelCategory(session,categoryId);
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getChildrenDeepCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        return iCategoryService.getChildrenDeepCategory(session,categoryId);
    }

}
