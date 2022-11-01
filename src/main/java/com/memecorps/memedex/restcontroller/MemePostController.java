package com.memecorps.memedex.restcontroller;

import com.memecorps.memedex.model.MemePost;
import com.memecorps.memedex.repository.MemePostRepository;
import com.memecorps.memedex.socketcontroller.MemeSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/memepost")
@CrossOrigin
public class MemePostController {

    @Autowired
    MemePostRepository memePostRepository;

    @Autowired
    MemeSocketController memeSocketController;

    @GetMapping({"/", ""})
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        try {
            Pageable paging = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

            Page<MemePost> pageMemePosts = memePostRepository.findAll(paging);

            Map<String, Object> response = new HashMap<>();
            response.put("content", pageMemePosts.getContent());
            response.put("number", pageMemePosts.getNumber());
            response.put("totalElements", pageMemePosts.getTotalElements());
            response.put("totalPages", pageMemePosts.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping({"/{id}"})
    public ResponseEntity getById(@PathVariable String id) {
        var memePostOptional = memePostRepository.findById(id);

        if (memePostOptional.isPresent()) {
            return ResponseEntity.ok(memePostOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping({"/", ""})
    public ResponseEntity createPost(@Valid @RequestBody MemePost memePost) {
        Optional<MemePost> memePostOptional = Optional.ofNullable(null);

        if (memePost.getId() != null) {
            memePostOptional = memePostRepository.findById(memePost.getId());
        }

        if (memePostOptional.isEmpty()) {
            memePost.setId(null);
            memePost.setTimestamp(System.currentTimeMillis());

            memeSocketController.sendNewMeme(memePost);

            return ResponseEntity.ok(memePostRepository.save(memePost));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity deleteById(@PathVariable String id) {
        var memePostOptional = memePostRepository.findById(id);

        if (memePostOptional.isPresent()) {
            memePostRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping({"/{id}"})
    public ResponseEntity updateById(@PathVariable String id, @Valid @RequestBody MemePost memePost) {
        var memePostOptional = memePostRepository.findById(id);

        if (memePostOptional.isPresent()) {
            memePost.setId(id);

            return ResponseEntity.ok(memePostRepository.save(memePost));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
