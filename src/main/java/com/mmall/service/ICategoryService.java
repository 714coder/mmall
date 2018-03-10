package com.mmall.service;
/**
 * SUCEESS IS NOT FINAL,FAILURE IS NOT FATAL.IT IS THE COURAGE TO CONTINUE THAT COUNTS
 */

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 *@Author:yuantao
 *@Description:
 *@Date:Created in 14:38 2018/3/8
 */
public interface ICategoryService {
    ServerResponse<String> addCategory(HttpSession session, String categoryName, Integer parentId);

    ServerResponse<String> updateCategoryName(HttpSession session,Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session, Integer parentId);

    ServerResponse<List<Integer>> getChildrenDeepCategory(HttpSession session,Integer categoryId);
}
