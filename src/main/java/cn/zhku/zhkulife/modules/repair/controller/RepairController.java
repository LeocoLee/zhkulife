package cn.zhku.zhkulife.modules.repair.controller;

import cn.zhku.zhkulife.modules.admin.service.AdminService;
import cn.zhku.zhkulife.modules.repair.service.RepairService;
import cn.zhku.zhkulife.po.entity.Admin;
import cn.zhku.zhkulife.po.entity.Repair;
import cn.zhku.zhkulife.po.entity.Water;
import cn.zhku.zhkulife.utils.Beans.Message;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * @author 钱伟健 gonefutre
 * @date 2017/8/1 22:48.
 * @E-mail gonefuture@qq.com
 */

@Controller
public class RepairController  {
    @Autowired
    private RepairService repairService;

    @Autowired
    AdminService adminService;

    @RequestMapping("repair/list")
    @ResponseBody
    public PageInfo<Repair>list(String pageNum, String pageSize, Repair repair) throws Exception {
        if (pageNum == null)
            pageNum = "1";
        if (pageSize == null)
            pageSize = "10";
        PageHelper.startPage(Integer.valueOf(pageNum),Integer.valueOf(pageSize));

        return new PageInfo<Repair>(repairService.findAll(repair));
    }

    @RequestMapping("repair/bookRepair")
    @ResponseBody
    public Message bookRepair(Repair repair,MultipartFile repairPic) throws Exception {
        repair.setRepairId(UUID.randomUUID().toString());
        repair.setRepairTime(new Date());
        repair.setRepairState(1);
        if(repairPic !=null) {
            //储存图片的物理路径
            String pic_path = "D:\\Java\\temp\\";
            //原始名称
            String originalFileName = repairPic.getOriginalFilename();
            //新的的图片名称
            String newFileName = UUID.randomUUID()+originalFileName.substring(originalFileName.lastIndexOf("."));
            //新图片文件
            File newFile = new java.io.File(pic_path+newFileName);
            //将内存中的数据写入磁盘
            repairPic.transferTo(newFile);
            //将新图片名称写到repair中
            repair.setRepairPic(newFileName);
        } else {
            return new Message("1","图片上传失败");
        }

        if ( repairService.add(repair) !=1 )
            return new Message("2","报修失败,信息填写不正确");
        else
            return new Message("1","报修成功");
    }

    @RequestMapping("repair/takeRepair")
    @ResponseBody
    public Message takeRepair(String repairId,String adminId) throws Exception {
        Repair repair = new Repair();
        repair.setRepairId(repairId); repair.setRepairState(2);
        if (repairService.update(repair) != 1)
            return new Message("2","接单失败，请核实订单数据");
        else
            return new Message("1","接单成功，请尽快维修");
    }

    @RequestMapping("repair/finishRepair")
    @ResponseBody
    public Message finishRepair(String repairId) throws Exception {
        Repair repair = new Repair();
        repair.setRepairId(repairId); repair.setRepairState(3);
        if(repairService.update(repair) != 1)
            return new Message("2","报修未完成,信息填写不正确");
        else
            return new Message("1","报修完成");
    }

    @RequestMapping("repair/feedbackRepair")
    @ResponseBody
    public Message feedbackRepair(String repairId, String feedback) throws Exception {
        Repair repair = new Repair();
        repair.setRepairId(repairId); repair.setRepairFeedback(Integer.valueOf(feedback));
        if(repairService.update(repair) != 1)
            return new Message("2","评价失败，请检验参数");
        else
            return new Message("1","评价完成");
    }

    @RequestMapping("repair/updatePassword")
    @ResponseBody
    public Message updatePassword(String password) throws Exception {
        Admin admin = new Admin();
        Subject subject = SecurityUtils.getSubject();
        Admin adminCache = (Admin) subject.getPrincipal();
        admin.setAdminPassword(password);
        if (adminService.update(admin) != 1)
            return new Message("2","修改密码失败，请检查参数");
        else
        return new Message("1","修改密码成功");
    }

    @RequestMapping("repair/updatePhone")
    @ResponseBody
    public Message updatePhone(String phone) throws Exception {
        Admin admin = new Admin();
        Subject subject = SecurityUtils.getSubject();
        Admin adminCache = (Admin) subject.getPrincipal();
        admin.setAdminId(adminCache.getAdminId());
        if (adminService.update(admin) != 1 )
            return new Message("2","修改手机号码失败");
        else {
            admin.setAdminPhone(phone);
            return new Message("1", "修改手机号码成功");
        }
    }


    }
