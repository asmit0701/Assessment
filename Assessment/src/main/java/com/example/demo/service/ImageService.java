package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import com.example.demo.model.Image;
import com.example.demo.model.User;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.ImageUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    public String uploadImage(MultipartFile imageFile) throws IOException {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String username = auth.getName();
    	User currentUser=userRepository.findByEmail(username).orElseThrow();
        var imageToSave = Image.builder()
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .imageData(ImageUtils.compressImage(imageFile.getBytes()))
                .user(currentUser)
                .build();
        imageRepository.save(imageToSave);
        return "file uploaded successfully : " + imageFile.getOriginalFilename();
    }

    public List<byte[]> showPhotos() throws DataFormatException, IOException {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String username = auth.getName();
    	User currentUser=userRepository.findByEmail(username).orElseThrow();
    	List<Image> dbImage=imageRepository.findAllByUser(currentUser);
    	List<byte[]> res=new ArrayList<byte[]>();
    	for(int i=0;i<dbImage.size();i++) {
    		res.add(ImageUtils.decompressImage(dbImage.get(i).getImageData()));
    	}
    	return res;
    }
}