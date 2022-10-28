package com.memecorps.memedex.socketcontroller;

import com.memecorps.memedex.model.MemePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MemeSocketController {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendNewMeme(MemePost newMeme) {
        this.template.convertAndSend("/memePost/newMeme", newMeme);
    }
}
