package com.pinyincentre.pinyin.service;

import com.pinyincentre.pinyin.dto.response.AdminDashboardResponse;
import com.pinyincentre.pinyin.dto.response.ClassSizeData;
import com.pinyincentre.pinyin.dto.response.TimeSeriesData;
import java.util.List;

public interface AdminDashboardService {
    AdminDashboardResponse getDashboardStats();
    List<TimeSeriesData> getRegistrationStats(String period);
    List<TimeSeriesData> getCourseEnrollmentStats();
    List<TimeSeriesData> getStudentStatusStats();
    List<ClassSizeData> getClassSizeStats();
}
