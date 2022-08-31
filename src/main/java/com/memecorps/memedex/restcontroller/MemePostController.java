package com.memecorps.memedex.restcontroller;

import com.memecorps.memedex.model.MemePost;
import com.memecorps.memedex.repository.MemePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memepost")
public class MemePostController {

    @Autowired
    MemePostRepository memePostRepository;

    @GetMapping({"/", ""})
    public List<MemePost> getAll() {
        return memePostRepository.findAll();
    }

    @GetMapping({"/{id}"})
    public ResponseEntity getById(@PathVariable String id) {
        var memePostOptional = memePostRepository.findById(id);

        if (memePostOptional.isPresent()) {
            return ResponseEntity.ok(memePostOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping({"/", ""})
    public MemePost createPost(@RequestBody MemePost memePost) {
        return memePostRepository.save(memePost);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity deleteById(@PathVariable String id) {
        var memePostOptional = memePostRepository.findById(id);

        if (memePostOptional.isPresent()) {
            memePostRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping({"/{id}"})
    public ResponseEntity updateById(@PathVariable String id, @RequestBody MemePost memePost) {
        var memePostOptional = memePostRepository.findById(id);

        if (memePostOptional.isPresent()) {
            return ResponseEntity.ok(memePostRepository.save(memePost));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
