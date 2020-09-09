#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;





extern "C"
JNIEXPORT void JNICALL
Java_com_example_msg_1b_checkmate_OpencvActivity_ConvertRGBtoGray(JNIEnv *env,
                                                            jobject instance,
                                                            jlong matAddrInput,
                                                            jlong matAddrResult)
{

    // TODO
    // 입력 RGBA 이미지를 GRAY 이미지로 변환
    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);

}


float resize(Mat img_src, Mat &img_resize, int resize_width) {

    float scale = resize_width / (float)img_src.cols;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    }
    else {
        img_resize = img_src;
    }
    return scale;


}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_msg_1b_checkmate_OpencvActivity_loadCascade(JNIEnv *env, jobject instance,
                                                       jstring cascadeFileName_) {
//    const char *cascadeFileName = env->GetStringUTFChars(cascadeFileName_, 0);
    // TODO

    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);
    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if(((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
        "CascadeClassifier로 로딩 실패 %s", nativeFileNameString);
    } else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
        "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);

    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;






//    env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}




extern "C"
JNIEXPORT jint JNICALL
Java_com_example_msg_1b_checkmate_OpencvActivity_detect(JNIEnv *env, jobject instance,
                                                  jlong cascadeClassifier_face,
                                                  jlong cascadeClassifier_eye,
                                                  jlong matAddrInput,
                                                  jlong matAddrResult) {

    // TODO

    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    //추가
    int ret = 0;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );

    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());

    //추가
    ret = faces.size();

//    for (int i=0; i<faces.size(); i++) {
//        double real_facesize_x = faces[i].x / resizeRatio;
//        double real_facesize_y = faces[i].y / resizeRatio;
//        double real_facesize_width = faces[i].width / resizeRatio;
//        double real_facesize_height = faces[i].height / resizeRatio;
//
//        Point center( real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height/2);
//        ellipse(img_result, center, Size( real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360, Scalar(255, 0, 255), 30, 8, 0);
//
//        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);
//        Mat faceROI = img_gray(face_area);
//        std::vector<Rect> eyes;
//
//        //-- In each face, detect eyes
//        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CASCADE_SCALE_IMAGE, Size(30, 30) );
//
//        for ( size_t j=0; j<eyes.size(); j++) {
//            Point eye_center( real_facesize_x + eyes[j].x + eyes[j].width/2,
//                              real_facesize_y + eyes[j].y + eyes[j].height/2 );
//            int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
//            circle( img_result, eye_center, radius, Scalar( 255, 0, 0 ), 30, 8, 0);
//        }
//
//    }

    return ret;




}













/*
 * Opencv2Activity
 *
 * */
extern "C"
JNIEXPORT void JNICALL
Java_com_example_msg_1b_checkmate_Opencv2Activity_loadImage(JNIEnv *env, jobject instance,
                                                               jstring imagePath_,
                                                               jlong nativeObjAddr) {
    const char *imagePath = env->GetStringUTFChars(imagePath_, 0);
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "로드이미지1");

    // TODO
    Mat &img_input = *(Mat *) nativeObjAddr;
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "로드이미지2");

    img_input = imread(imagePath, IMREAD_COLOR);
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "로드이미지3");


    env->ReleaseStringUTFChars(imagePath_, imagePath);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_msg_1b_checkmate_Opencv2Activity_imageprocessing(JNIEnv *env, jobject instance,
                                                                     jlong nativeObjAddr,
                                                                     jlong nativeObjAddr1) {

    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "이미지프로세스1");
    // TODO
    Mat &img_input = *(Mat *) nativeObjAddr;
    Mat &img_output = *(Mat *) nativeObjAddr1;
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "이미지프로세스2");
    cvtColor( img_input, img_input, COLOR_BGR2RGB);
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "이미지프로세스3");
    cvtColor( img_input, img_output, COLOR_RGB2GRAY);
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "이미지프로세스4");
    blur( img_output, img_output, Size(5,5));
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "이미지프로세스5");
    Canny( img_output, img_output, 50, 150, 5);
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "opencv3 :: ",
                        (char *) "이미지프로세스6");



}



extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_msg_1b_checkmate_videocall_CallActivity_loadCascade(JNIEnv *env, jobject instance,
                                                                        jstring cascadeFileName_) {
//    const char *cascadeFileName = env->GetStringUTFChars(cascadeFileName_, 0);
    // TODO

    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);
    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if(((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패 %s", nativeFileNameString);
    } else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);

    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;






//    env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}




extern "C"
JNIEXPORT jint JNICALL
Java_com_example_msg_1b_checkmate_videocall_CallActivity_detect(JNIEnv *env, jobject instance,
                                                                   jlong cascadeClassifier_face,
                                                                   jlong cascadeClassifier_eye,
                                                                   jlong matAddrInput,
                                                                   jlong matAddrResult) {

    // TODO

    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    //추가
    int ret = 0;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );

    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());

    //추가
    ret = faces.size();

    return ret;




}

