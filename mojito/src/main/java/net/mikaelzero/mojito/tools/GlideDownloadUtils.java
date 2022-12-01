//package net.mikaelzero.mojito.tools;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.provider.Settings;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.FragmentActivity;
//
//import com.blankj.utilcode.util.FileUtils;
//import com.blankj.utilcode.util.PathUtils;
//import com.blankj.utilcode.util.ToastUtils;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
//import com.tbruyelle.rxpermissions2.RxPermissions;
//
//import java.io.File;
//
//import io.reactivex.disposables.Disposable;
//
///**
// * @author Created by jinshanshan on 2022/10/10 18:26
// */
//
//
//public class GlideDownloadUtils {
//
//    private static Disposable mDisposable = null;
//
//    @SuppressLint("CheckResult")
//    public static void toDownload(FragmentActivity context, String url) {
//
//        mDisposable = new RxPermissions(context)
//                .requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .subscribe(permission -> {
//                            if (permission.granted) {
//                                saveImgToLocal(context, url);
//                            } else if (permission.shouldShowRequestPermissionRationale) {
//                                ToastUtils.showLong("获取文件读写权限失败，请打开权限后在尝试！");
//                            } else {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                builder.setMessage("您尚未授予文件读写权限或存储权限，是否前往设置页面打开？");
//                                builder.setTitle("提示");
//                                builder.setPositiveButton("确定", (dialog, which) -> {
//                                    dialog.dismiss();
//                                    startToSetting(context);
//                                });
//                                builder.setNegativeButton("取消", (dialog, which) -> {
//                                    dialog.dismiss();
//                                });
//                                builder.create().show();
//                            }
//                        }, throwable -> {
//                            ToastUtils.showLong("获取文件读写权限失败，请打开权限后在尝试！");
//                        }
//                );
//    }
//
//    private static void saveImgToLocal(FragmentActivity context, String url) {
//        //如果是网络图片，抠图的结果，需要先保存到本地
//        Glide.with(context)
//                .downloadOnly()
//                .load(url)
//                .listener(new RequestListener<File>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
//                        Toast.makeText(context, "图片保存到相册成功", Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
//                        saveToAlbum(context, resource.getAbsolutePath());
//                        return false;
//                    }
//                })
//                .preload();
//
//    }
//
//
//    /**
//     * 保存到相册中
//     *
//     * @param context 上下文
//     * @param srcPath 网络图保存到本地的缓存文件路径
//     */
//    private static void saveToAlbum(Context context, String srcPath) {
//        String dcimPath = PathUtils.getExternalDcimPath();
//        File file = new File(dcimPath, "content_" + System.currentTimeMillis() + ".png");
//        boolean isCopySuccess = FileUtils.copy(srcPath, file.getAbsolutePath());
//        if (isCopySuccess) {
//            //发送广播通知
//            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
//            ToastUtils.showShort("图片保存到相册成功");
//        } else {
//            ToastUtils.showShort("图片保存到相册失败");
//        }
//    }
//
//    private static void startToSetting(@NonNull Context context) {
//        try {
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
//            context.startActivity(intent);
//        } catch (Exception e) {
//            ToastUtils.showLong("无法打开设置页面，请您去设置页面自行设置！");
//        }
//    }
//}
