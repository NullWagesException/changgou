package com.changgou.utils;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: nullWagesException
 * @Date: 2019/12/28 1:21
 * @Description:
 * 实现FastDFS文件管理
 * 文件上传
 * 文件下载
 * 文件删除
 * 文件信息获取
 * storage信息获取
 * tracker信息获取
 *
 *
 */
public class FastDFSUtils {


    /**
     * 加载tracker信息
     */
    static{
        String path = new ClassPathResource("fdfs_client.conf").getPath();
        try {
            ClientGlobal.init(path);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
    }

    //图片下载
    public static InputStream downFile(String groupName, String remoteFileName) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            TrackerServer trackerServer = getTrackerServer();
            StorageClient storageClient = getStorageClient(trackerServer);

            //参数1:指定组名
            //参数2 :指定远程的文件名
            byte[] bytes = storageClient.download_file(groupName, remoteFileName);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            return byteArrayInputStream;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static TrackerServer getTrackerServer() throws Exception{
        return new TrackerClient().getConnection();
    }

    public static StorageClient getStorageClient(TrackerServer trackerServer) throws Exception{
        return new StorageClient(trackerServer, null);
    }

    //图片删除

    public static void deleteFile(String groupName, String remoteFileName) {
        try {
            TrackerServer trackerServer = getTrackerServer();
            StorageClient storageClient = getStorageClient(trackerServer);
            int i = storageClient.delete_file(groupName, remoteFileName);
            if (i == 0) {
                System.out.println("删除成功");
            } else {
                System.out.println("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件上传
     * @param fastDFSFile 上传的文件信息
     * @return
     */
    public static String[] upload (FastDFSFile fastDFSFile) throws Exception{
        //附加参数
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", fastDFSFile.getAuthor());

        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = getStorageClient(trackerServer);
        /**
         * 通过 Storageclient访问 Storage,实现文件上传,并且获取文件上传后的存储信息
         * 1：上传的字节数组
         * 2：文件的扩展名
         * 3：附加参数
         */
        String[] upload = storageClient.upload_appender_file(fastDFSFile.getContent(), fastDFSFile.getExt(), meta_list);

        return upload;

    }



    //根据组名获取组的信息

    public static StorageServer getStorages(String groupName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            //参数1 指定traqckerserver 对象
            //参数2 指定组名
            StorageServer group1 = trackerClient.getStoreStorage(trackerServer, groupName);
            return group1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //根据文件名和组名获取文件的信息

    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            TrackerServer trackerServer = getTrackerServer();
            StorageClient storageClient = getStorageClient(trackerServer);
            //参数1 指定组名
            //参数2 指定文件的路径
            FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //根据文件名和组名 获取组信息的数组信息
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName){
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
            return group1s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    //获取tracker 的ip和端口的信息
    public static String getTrackerUrl(){
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //tracker 的ip的信息
            String hostString = trackerServer.getInetSocketAddress().getHostString();

            int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();
            return "http://" + hostString + ":" + g_tracker_http_port;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
