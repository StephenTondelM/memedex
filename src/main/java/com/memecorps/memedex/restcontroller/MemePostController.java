package com.memecorps.memedex.restcontroller;

import com.memecorps.memedex.model.MemePost;
import com.memecorps.memedex.repository.MemePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/memepost")
@CrossOrigin
public class MemePostController {

    @Autowired
    MemePostRepository memePostRepository;

    @GetMapping({"/", ""})
    public ResponseEntity getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        try {
            List<MemePost> memePosts = new ArrayList<>();
            Pageable paging = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

            Page<MemePost> pageMemePosts = memePostRepository.findAll(paging);

            memePosts = pageMemePosts.getContent();

            return ResponseEntity.ok(memePosts);
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
    public ResponseEntity createPost(@RequestBody MemePost memePost) {
        Optional<MemePost> memePostOptional = Optional.ofNullable(null);

        if (memePost.getId() != null) {
            memePostOptional = memePostRepository.findById(memePost.getId());
        }

        if (memePostOptional.isEmpty()) {
            memePost.setId(null);
            memePost.setTimestamp(System.currentTimeMillis());

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
    public ResponseEntity updateById(@PathVariable String id, @RequestBody MemePost memePost) {
        var memePostOptional = memePostRepository.findById(id);

        if (memePostOptional.isPresent()) {
            memePost.setId(id);

            return ResponseEntity.ok(memePostRepository.save(memePost));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
