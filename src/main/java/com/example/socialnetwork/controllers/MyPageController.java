package com.example.socialnetwork.controllers;

import com.example.socialnetwork.models.UserEntity;
import com.example.socialnetwork.models.Valute;
import com.example.socialnetwork.service.FileService;
import com.example.socialnetwork.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/my_page")
public class MyPageController {

    @Value("${application.avatar-folder}")
    private String avatarFolder;

    @Value("${application.cover-folder}")
    private String coverFolder;

    private UserService userService;
    private FileService fileService;

    @Autowired
    public MyPageController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @GetMapping
    public String showMyPage(Model model) {
        log.info("showMyPage method called");

        Map<String, Valute> valutes = userService.getAllValutes();
        for(Map.Entry<String, Valute> valute : valutes.entrySet()) {
            String name = valute.getKey();

            if(name.equals("USD")) {
                model.addAttribute("USD", Math.round(valute.getValue().getValue()));
            }

            if(name.equals("EUR")) {
                model.addAttribute("EUR", Math.round(valute.getValue().getValue()));
            }
        }

        UserEntity currentUser = userService.getAnAuthorizedUser();
        currentUser.setOnline(true);

        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("name", currentUser.getName());
        model.addAttribute("surname", currentUser.getSurname());
        model.addAttribute("avatar", currentUser.getAvatar());
        model.addAttribute("status", currentUser.getStatus());
        model.addAttribute("email", currentUser.getEmail());
        model.addAttribute("birthday", currentUser.getDateOfBirth());
        model.addAttribute("city", currentUser.getCity());
        model.addAttribute("online", currentUser.getOnline());

        return "my_page";
    }

    @GetMapping("/upload_avatar_form")
    public String getUploadAvatarForm(Model model) {
        log.info("getUploadAvatarForm method called");
        model.addAttribute("avatar", userService.getAnAuthorizedUser().getAvatar());

        return "upload_avatar_form";
    }

    @PostMapping("/upload_avatar_form")
    public String submitUploadAvatarForm(@RequestParam("file") MultipartFile avatar, Model model) {
        log.info("submitUploadAvatarForm method called");

//        if(bindingResult.hasErrors()){
//            log.error("Some errors in submitUploadAvatarForm method", bindingResult.hasErrors());
//            return "edit_page";
//        }

        UserEntity currentUser = userService.getAnAuthorizedUser();
        currentUser.setAvatar(fileService.uploadImage(avatar, Paths.get(coverFolder)));

        userService.updateUser(currentUser);

        return "redirect:/my_page";
    }

    @GetMapping("/edit_page")
    public String editInfoForm(Model model) {
        log.info("editInfoForm method called");
        return "edit_page";
    }

    @PostMapping("/edit_page")
    public String editInfoFormSubmit(Model model, UserEntity newUserEntity) {
        log.info("editInfoFormSubmit method called");

//        if(bindingResult.hasErrors()){
//            log.error("Some errors in editInfoFormSubmit method", bindingResult.hasErrors());
//            return "edit_page";
//        }
        userService.updateUser(newUserEntity);

        return "redirect:/my_page";
    }

    @GetMapping("/upload_cover_form")
    public String getUploadCoverForm(Model model) {
        log.info("getUploadCoverForm method called");
        return "upload_cover_form";
    }
}
