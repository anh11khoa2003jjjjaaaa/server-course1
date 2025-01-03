//
//package org.example.sellingcourese.Service;
//
//import org.example.sellingcourese.Model.Course;
//import org.example.sellingcourese.repository.CartDetailRepository;
//import org.example.sellingcourese.repository.CourseRepository;
//import org.example.sellingcourese.repository.OrderDetailRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.http.HttpStatus;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class CourseService {
//
//    @Autowired
//    private CourseRepository courseRepository;
//
//    @Autowired
//    private CartDetailRepository cartDetailRepository;
//    @Autowired
//    private OrderDetailRepository orderItemRepository;
//    @Value("${video.upload-dir}") // Sử dụng key đúng từ application.properties
//    private String uploadDir;
//
//    // Get all courses
//    public List<Course> getAllCourses() {
//        return courseRepository.findAll();
//    }
//
//    // Add course with files
//    public Course addCourseWithFiles(String title, String description, BigDecimal price, Long teacherId, Long categoryId,
//                                     MultipartFile thumbnail, MultipartFile video) {
//        String thumbnailUrl = saveFile(thumbnail);
//        String videoUrl = saveFile(video);
//
//        Course course = new Course();
//        course.setTitle(title);
//        course.setDescription(description);
//        course.setPrice(price);
//        course.setTeacherId(teacherId);
//        course.setCategoryId(categoryId);
//        course.setThumbnailUrl(thumbnailUrl);
//        course.setVideoUrl(videoUrl);
//
//        return courseRepository.save(course);
//    }
//
//    // Update course with files
//    public Course updateCourseWithFiles(Long id, String title, String description, BigDecimal price, Long teacherId,
//                                        Long categoryId, MultipartFile thumbnail, MultipartFile video) {
//        Optional<Course> optionalCourse = courseRepository.findById(id);
//        if (optionalCourse.isPresent()) {
//            Course course = optionalCourse.get();
//            course.setTitle(title);
//            course.setDescription(description);
//            course.setPrice(price);
//            course.setTeacherId(teacherId);
//            course.setCategoryId(categoryId);
//
//            if (thumbnail != null && !thumbnail.isEmpty()) {
//                course.setThumbnailUrl(saveFile(thumbnail));
//            }
//
//            if (video != null && !video.isEmpty()) {
//                course.setVideoUrl(saveFile(video));
//            }
//
//            return courseRepository.save(course);
//        } else {
//            throw new RuntimeException("Course not found with ID: " + id);
//        }
//    }
//
//    @Transactional
//    public void deleteCourse(Long id) {
//        if (!courseRepository.existsById(id)) {
//            throw new RuntimeException("Course not found with ID: " + id);
//        }
//
//        // Xóa các CartDetails liên quan đến Course
//        cartDetailRepository.deleteByCourseID(id);
//
//        // Xóa các OrderItems liên quan đến Course
//        orderItemRepository.deleteByCourseId(id);
//
//        // Cuối cùng, xóa Course
//        courseRepository.deleteById(id);
//    }
//
//
//    // Get course by ID
//    public Course getCourseById(Long id) {
//        return courseRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
//    }
//
//    // Find courses by title
//    public List<Course> findCoursesByTitle(String title) {
//        return courseRepository.findByTitleContainingIgnoreCase(title);
//    }
//
//    public Resource getVideoStream(Long id) {
//        Course course = getCourseById(id);
//        String videoUrl = course.getVideoUrl();  // Lấy đường dẫn video từ course
//
//        // Kiểm tra nếu videoUrl không tồn tại
//        if (videoUrl == null || videoUrl.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not available for this course.");
//        }
//
//        try {
//            // Chuyển đường dẫn video thành một đối tượng Path
//            Path path = Paths.get(videoUrl).toAbsolutePath();  // Sử dụng videoUrl trực tiếp vì bạn đã lưu đường dẫn tuyệt đối
//            Resource resource = new UrlResource(path.toUri());  // Tạo UrlResource từ đường dẫn
//
//            // Kiểm tra nếu video tồn tại và có thể đọc được
//            if (resource.exists() && resource.isReadable()) {
//                return resource;
//            } else {
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video file not found.");
//            }
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading video file.", e);
//        }
//    }
//
//    private String saveFile(MultipartFile file) {
//        if (file == null || file.isEmpty()) {
//            return null;
//        }
//        try {
//            // Kiểm tra đường dẫn thư mục
//            Path uploadDirPath = Paths.get(uploadDir).toAbsolutePath();
//            System.out.println("Upload directory: " + uploadDirPath);
//
//            Path filePath = Paths.get(uploadDir, file.getOriginalFilename()).toAbsolutePath();
//            Files.copy(file.getInputStream(), filePath);
//            return filePath.toString(); // Trả về đường dẫn tuyệt đối hoặc URL của file
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
//        }
//    }
//
//}
package org.example.sellingcourese.Service;

import org.example.sellingcourese.Model.Course;
import org.example.sellingcourese.repository.CartDetailRepository;
import org.example.sellingcourese.repository.CourseRepository;
import org.example.sellingcourese.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;
    @Autowired
    private OrderDetailRepository orderItemRepository;
    @Value("${video.upload-dir}") // Sử dụng key đúng từ application.properties
    private String uploadDir;

    // Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Add course with files
    public Course addCourseWithFiles(String title, String description, BigDecimal price, Long teacherId, Long categoryId,
                                     MultipartFile thumbnail, MultipartFile video) {
        String thumbnailUrl = saveFile(thumbnail);
        String videoUrl = saveFile(video);

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setPrice(price);
        course.setTeacherId(teacherId);
        course.setCategoryId(categoryId);
        course.setThumbnailUrl(thumbnailUrl);
        course.setVideoUrl(videoUrl);
        course.setStatus(1); // Mặc định là chờ xử lý

        return courseRepository.save(course);
    }

    // Update course with files
    public Course updateCourseWithFiles(Long id, String title, String description, BigDecimal price, Long teacherId,
                                        Long categoryId, MultipartFile thumbnail, MultipartFile video) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setTitle(title);
            course.setDescription(description);
            course.setPrice(price);
            course.setTeacherId(teacherId);
            course.setCategoryId(categoryId);

            if (thumbnail != null && !thumbnail.isEmpty()) {
                course.setThumbnailUrl(saveFile(thumbnail));
            }

            if (video != null && !video.isEmpty()) {
                course.setVideoUrl(saveFile(video));
            }

            return courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with ID: " + id);
        }

        // Xóa các CartDetails liên quan đến Course
        cartDetailRepository.deleteByCourseID(id);

        // Xóa các OrderItems liên quan đến Course
        orderItemRepository.deleteByCourseId(id);

        // Cuối cùng, xóa Course
        courseRepository.deleteById(id);
    }

    // Get course by ID
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
    }

    // Find courses by title
    public List<Course> findCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    public Resource getVideoStream(Long id) {
        Course course = getCourseById(id);
        String videoUrl = course.getVideoUrl();  // Lấy đường dẫn video từ course

        // Kiểm tra nếu videoUrl không tồn tại
        if (videoUrl == null || videoUrl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not available for this course.");
        }

        try {
            // Chuyển đường dẫn video thành một đối tượng Path
            Path path = Paths.get(videoUrl).toAbsolutePath();  // Sử dụng videoUrl trực tiếp vì bạn đã lưu đường dẫn tuyệt đối
            Resource resource = new UrlResource(path.toUri());  // Tạo UrlResource từ đường dẫn

            // Kiểm tra nếu video tồn tại và có thể đọc được
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video file not found.");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading video file.", e);
        }
    }

    private String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null; // Nếu file null hoặc rỗng, trả về null
        }
        try {
            // Kiểm tra đường dẫn thư mục
            Path uploadDirPath = Paths.get(uploadDir).toAbsolutePath();
            System.out.println("Upload directory: " + uploadDirPath);

            // Lấy tên gốc của file
            String originalFilename = file.getOriginalFilename();

            // Tách phần mở rộng (nếu có)
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Tạo tên file mới bằng cách thêm timestamp
            String newFilename = System.currentTimeMillis() + fileExtension;

            // Đường dẫn file mới
            Path filePath = Paths.get(uploadDir, newFilename).toAbsolutePath();

            // Lưu file vào thư mục
            Files.copy(file.getInputStream(), filePath);

            // Trả về đường dẫn tuyệt đối của file
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }


    // Update course status
    public Course updateCourseStatus(Long id, Integer status) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setStatus(status);
            return courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }

    public Course updateCancelReason(Long id, String cancelReason,Integer status) {
Optional<Course>optionalCourse=courseRepository.findById(id);
        if(optionalCourse.isPresent()){
            Course course=optionalCourse.get();
            course.setStatus(status);
            course.setCancelReason(cancelReason);
            return courseRepository.save(course);
        }else{
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }

    // Get courses by status
    public List<Course> getCoursesByStatus(Integer status) {
        return courseRepository.findByStatus(status);
    }

}