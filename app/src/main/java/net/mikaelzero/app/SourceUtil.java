package net.mikaelzero.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SourceUtil {


    public static List<String> getNormalImages() {
        List<String> list = new ArrayList<>();
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042332605-assets/web-upload/1af8e4c0-bf8b-410a-bfff-a16fec01ccb5.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1591710912974-assets/web-upload/1e6325b7-4e26-443f-98f8-aa3925222ea1.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1588042170204-assets/web-upload/48a5152a-5024-43fd-bd50-796d6f284e77.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042333257-assets/web-upload/dfe8a4eb-9872-444b-b2a5-83378f467915.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1591753659216-assets/web-upload/2c772338-b6b6-4173-a830-202831511172.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042333210-assets/web-upload/8d20ed3d-1472-47c9-a2e6-da96e6019299.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042333165-assets/web-upload/cde12f44-07bb-46aa-ab7d-0ced4783b2ee.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/png/252337/1587091196083-assets/web-upload/62122ab5-986b-4662-be88-d3007a5e31c5.png");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592288524369-assets/web-upload/84ee5f6c-8459-4497-8175-153836bef167.jpeg");
        return list;
    }

    public static List<String> getTestImages() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i <2000; i++) {
            list.addAll(getNormalImages());
        }
        return list;
    }

    public static List<String> getVideoImages() {
        List<String> list = new ArrayList<>();
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042332605-assets/web-upload/1af8e4c0-bf8b-410a-bfff-a16fec01ccb5.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/png/252337/1592812206070-assets/web-upload/07a534d9-8f24-492b-b659-a76aac4e20f8.png");
        list.add("https://cdn.nlark.com/yuque/0/2020/png/252337/1592812206070-assets/web-upload/07a534d9-8f24-492b-b659-a76aac4e20f8.png");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1588042170204-assets/web-upload/48a5152a-5024-43fd-bd50-796d6f284e77.jpeg");
        return list;
    }

    public static String getSingleVideoImages() {
        return "https://cdn.nlark.com/yuque/0/2020/png/252337/1592812206070-assets/web-upload/07a534d9-8f24-492b-b659-a76aac4e20f8.png";
    }

    public static List<String> getVideoTargetImages() {
        List<String> list = new ArrayList<>();
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592042332605-assets/web-upload/1af8e4c0-bf8b-410a-bfff-a16fec01ccb5.jpeg");
        list.add(videoUrl());
        list.add("http://vfx.mtime.cn/Video/2018/07/06/mp4/180706094003288023.mp4");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1588042170204-assets/web-upload/48a5152a-5024-43fd-bd50-796d6f284e77.jpeg");
        return list;
    }

    public static String getSingleVideoTargetImages() {
        return videoUrl();
    }

    public static String videoUrl() {
        return "https://apd-8d51891fce5fc22e6bb1ec1f711fc97b.v.smtcdns.com/mv.music.tc.qq.com/AV32c3rihanK4crv62XO5c_uI3uURafaVFT27KI_Y6AE/97A73ABFCADF3AFDFDDA1AF6BD3D6C0C0444ED0DC63C9A8FE1B0DEC94B360208D1C75AE5B284A44AF4FF4F9DE59BFAE1ZZqqmusic_default/1049_M0143700004FSwAW2Mxtku1001735049.f9844.mp4?fname=1049_M0143700004FSwAW2Mxtku1001735049.f9844.mp4";
    }

    public static String getSingleImage() {
        return "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1591753659216-assets/web-upload/2c772338-b6b6-4173-a830-202831511172.jpeg";
    }

    public static String getLongHorImage() {
        return "https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592057985345-assets/web-upload/c2fe2b62-5519-4129-856e-ba19428a508a.jpeg";
    }

    public static List<String> getTargetButtonTarget() {
        List<String> list = new ArrayList<>();
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592643441654-assets/web-upload/771e09b0-aaf9-4308-bae0-cd5b3cb98817.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592643441557-assets/web-upload/94ed7774-2bed-4dbe-be54-080c2f8939a1.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592643441632-assets/web-upload/84d01b3f-7f28-4125-b3c7-8e5b5b15c0cb.jpeg");
        return list;
    }

    public static List<String> getTargetButtonSmall() {
        List<String> list = new ArrayList<>();
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592643585276-assets/web-upload/6e2520de-544d-45b6-9892-ff228d14e175.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592643585330-assets/web-upload/85629ce4-bacf-4ab4-af6f-5a0a67931b68.jpeg");
        list.add("https://cdn.nlark.com/yuque/0/2020/jpeg/252337/1592643585344-assets/web-upload/3b178609-8aef-48f3-9ebc-41537149f13b.jpeg");
        return list;
    }

    public static List<String> getLatestPhotoPaths(Context context, int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = context.getContentResolver();
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/jpg", "image/gif"},
                MediaStore.Images.Media.DATE_MODIFIED);

        List<String> latestImagePaths = null;
        if (cursor != null) {
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();
                do {
                    // 获取图片的路径
                    String path = cursor.getString(0);
                    if (!latestImagePaths.contains(path))
                        latestImagePaths.add(path);

                } while (latestImagePaths.size() < maxCount && cursor.moveToPrevious());
            }
            cursor.close();
        }

        return latestImagePaths;
    }
}
