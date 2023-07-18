package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;


// 文件上传和下载

@RestController
@RequestMapping("/common")
public class CommonController {

    // 将配置文件中的属性值注入到对应的变量中
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    // MultipartFile file 参数表示接收前端传递的文件，这个file是一个临时文件，需要转存到指定的目录下
    public R<String> upload(MultipartFile file){
        //拿到文件的名字
        String originalFilename = file.getOriginalFilename();
        //拿到文件的后缀名 比如 .png  .jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid生成的作为文件名的一部分，这样可以防止文件名相同造成的文件覆盖
        // 这个名字就是随机生成字符串，咱也看不懂
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            //把前端传过来的文件进行转存,路径+文件名
            file.transferTo(new File(basePath + fileName));
        }catch (IOException e){
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    // HttpServletResponse 的作用是与客户端（一般是浏览器）进行交互，通过设置响应的状态码、头部信息、内容等，向客户端发送响应结果。
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            //设置写回去的文件类型
            // 这样做的目的是让客户端正确识别下载文件的类型，并相应地处理该文件。如果不设置正确的内容类型，浏览器可能无法正确解析文件类型，导致下载的文件无法正常显示或打开。
            response.setContentType("image/jpeg");
            //定义缓存区，准备读写文件
            int len  = 0 ;
            // buff是字节数组类型，因为在文件传输过程中，数据以字节形式进行读取和写入。字节数组提供了一种有效的方式来存储和处理字节数据。
            byte[] buff = new byte[1024];
            // 读取数据时，输入流的读取位置会不断向后移动。每次调用fileInputStream.read(buff)，并将输入流的读取位置向后移动到下一个可读位置。
            while ((len = fileInputStream.read(buff)) != -1){
                outputStream.write(buff,0,len);// 把缓冲区的[0,len]区间的数据写入输出流
                outputStream.flush();// 将缓冲区的内容强制刷新到输出流中，确保数据被及时发送到客户端。
            }
            outputStream.close();
            fileInputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
