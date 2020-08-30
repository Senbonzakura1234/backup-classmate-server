package com.app.manager.service.implementClass;

import com.app.manager.context.repository.CourseRepository;
import com.app.manager.context.repository.UserRepository;
import com.app.manager.context.specification.CourseSpecification;
import com.app.manager.entity.Course;
import com.app.manager.model.payload.CastObject;
import com.app.manager.model.payload.request.CourseRequest;
import com.app.manager.model.payload.response.CourseResponse;
import com.app.manager.model.returnResult.DatabaseQueryResult;
import com.app.manager.service.interfaceClass.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class CourseServiceImp implements CourseService {
    @Autowired CourseRepository courseRepository;
    @Autowired UserRepository userRepository;
    @Autowired CastObject castObject;

    @Override
    public List<CourseResponse> findAll(CourseSpecification courseSpecification) {
        try {
            var courses = courseRepository.findAll(courseSpecification);
            var list = new ArrayList<CourseResponse>();
            courses.forEach(course -> list.add(new CourseResponse(
                    course.getId(),
                    course.getUser_id(), course.getCourse_category_id(),
                    course.getName(), course.getDescription(),
                    course.getStart_date(), course.getEnd_date(),
                    course.getCreated_at(), course.getStatus())));
           return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public DatabaseQueryResult save(CourseRequest courseRequest, String currentUsername) {
        try {
            var teacher = userRepository.findByUsername(currentUsername);
            if(teacher.isEmpty())
                return new DatabaseQueryResult(false,
                        "Teacher not found", HttpStatus.NOT_FOUND, "");
            var course = castObject.courseEntity(courseRequest,
                    teacher.get().getId());
            courseRepository.save(course);
            return new DatabaseQueryResult(true,
                    "save course success", HttpStatus.OK,
                    castObject.courseModel(course));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

            return new DatabaseQueryResult(false,
                    "save course failed",
                    HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
    }

    @Override
    public Optional<CourseResponse> getOne(String id) {
        try {
            var course = courseRepository.findById(id);
            if(course.isEmpty()) return Optional.empty();
            return Optional.of(castObject.courseModel(course.get()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public DatabaseQueryResult update(CourseRequest courseRequest,
                                      String id, String currentUsername) {
        try {
            var teacher = userRepository.findByUsername(currentUsername);
            if(teacher.isEmpty())
                return new DatabaseQueryResult(false, "Teacher not found",
                        HttpStatus.NOT_FOUND, "");

            var c = courseRepository.findById(id);
            if(c.isEmpty()){
                return new DatabaseQueryResult(false,
                        "save course failed", HttpStatus.NOT_FOUND, "");
            }

            var course  = c.get();

            if(!course.getUser_id().equals(teacher.get().getId()))
                return new DatabaseQueryResult(false, "Not your course",
                        HttpStatus.BAD_REQUEST, "");

            course.setCourse_category_id(courseRequest.getCourse_category_id());
            course.setDescription(courseRequest.getDescription());
            course.setEnd_date(courseRequest.getEnd_date());
            course.setName(courseRequest.getName());
            course.setStart_date(courseRequest.getStart_date());
            courseRepository.save(course);

            return new DatabaseQueryResult(true,
                    "save course success", HttpStatus.OK,
                    castObject.courseModel(course));
        } catch (Exception e) {
            e.printStackTrace();
            return new DatabaseQueryResult(false,
                    "save course failed",
                    HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
    }

    @Override
    public DatabaseQueryResult updateStatus(String id, Course.StatusEnum status,
                                            String currentUsername) {
        try {
            var teacher = userRepository.findByUsername(currentUsername);
            if(teacher.isEmpty())
                return new DatabaseQueryResult(false, "Teacher not found",
                        HttpStatus.NOT_FOUND, "");

            var course = courseRepository.findById(id);
            if(course.isEmpty()){
                return new DatabaseQueryResult(false,
                        "update course failed", HttpStatus.NOT_FOUND, "");
            }
            if(!course.get().getUser_id().equals(teacher.get().getId()))
                return new DatabaseQueryResult(false, "Not your course",
                        HttpStatus.BAD_REQUEST, "");

            var c = course.get();
            c.setStatus(status);
            courseRepository.save(c);
            return new DatabaseQueryResult(true,
                    "update course success", HttpStatus.OK,
                    castObject.courseModel(c));
        }catch (Exception e){
            e.printStackTrace();
            return new DatabaseQueryResult(false,
                    "update course failed",
                    HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
    }
}
