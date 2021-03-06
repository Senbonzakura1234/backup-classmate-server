package com.app.manager.controller;

import com.app.manager.context.specification.ExerciseSpecification;
import com.app.manager.entity.Exercise;
import com.app.manager.model.SearchCriteria;
import com.app.manager.model.payload.request.ExerciseRequest;
import com.app.manager.model.payload.request.StudentExerciseRequest;
import com.app.manager.model.payload.response.MessageResponse;
import com.app.manager.service.interfaceClass.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data/exercise")
public class ExerciseController {
    @Autowired ExerciseService exerciseService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(value = "title", required = false, defaultValue = "") String title,
            @RequestParam(value = "session_id", required = false, defaultValue = "") String session_id,
            @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "status", required = false) Exercise.StatusEnum status
    ) {
        var query = new ExerciseSpecification();
        if(title != null){
            query.add(new SearchCriteria("title", title,
                    SearchCriteria.SearchOperation.MATCH));
        }

        if(session_id != null && !session_id.isEmpty()){
            query.add(new SearchCriteria("session_id", session_id,
                    SearchCriteria.SearchOperation.EQUAL));
        }

        if(content != null && !content.isEmpty()){
            query.add(new SearchCriteria("content", content,
                    SearchCriteria.SearchOperation.EQUAL));
        }

        if(status != null && status != Exercise.StatusEnum.ALL){
            query.add(new SearchCriteria("status", status.getValue(),
                    SearchCriteria.SearchOperation.EQUAL));
        }

        return ResponseEntity.ok(exerciseService.findAll(query));
    }

    @GetMapping("/detail")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getOne(@RequestParam(value = "id") String id) {
        var currentUser = SecurityContextHolder
                .getContext().getAuthentication().getName();
        var result = exerciseService.getOne(id, currentUser);
        if(result.isEmpty()) return ResponseEntity
                .status(HttpStatus.NOT_FOUND).body("Not found");
        return ResponseEntity.ok(result.get());
    }


    @PostMapping("/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> update(@Valid @RequestBody ExerciseRequest exerciseRequest,
                                    @RequestParam(value = "id") String id,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .forEach(System.out::println);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Validate Error"));
        }
        var currentUser = SecurityContextHolder
                .getContext().getAuthentication().getName();
        var result = exerciseService.update(exerciseRequest, id, currentUser);
        return result.isSuccess() ? ResponseEntity.ok(result.getDescription()) :
                ResponseEntity.status(result.getHttp_status()).body(result);
    }

    @PostMapping("/updateStatus")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateStatus(@RequestParam(value = "id") String id,
            @RequestParam(value = "status") Exercise.StatusEnum status){
        var currentUser = SecurityContextHolder
                .getContext().getAuthentication().getName();
        var result = exerciseService.updateStatus(id, status, currentUser);
        return result.isSuccess() ? ResponseEntity.ok(result.getDescription()) :
                ResponseEntity.status(result.getHttp_status()).body(result);
    }

    @PostMapping("/postStudentExercise")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> postExercise (
            @Valid @RequestBody StudentExerciseRequest studentExerciseRequest,
            @RequestParam(value = "exercise_id") String exercise_id,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .forEach(System.out::println);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Validate Error"));
        }
        var currentUser = SecurityContextHolder
                .getContext().getAuthentication().getName();
        var result = exerciseService
                .saveStudentExercise(studentExerciseRequest, exercise_id, currentUser);
        return result.isSuccess() ? ResponseEntity.ok(result.getDescription()) :
                ResponseEntity.status(result.getHttp_status()).body(result);
    }
}
