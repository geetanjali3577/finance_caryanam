package com.finserv.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {


    private static final String VERIFY_TOKEN = "finserv123";


    @GetMapping
    public String verify(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token
    ){

        if(mode.equals("subscribe")
                && token.equals(VERIFY_TOKEN)){

            return challenge;
        }

        return "Invalid token";
    }


    @PostMapping
    public String receive(
            @RequestBody String body
    ){

        System.out.println(body);

        return "EVENT_RECEIVED";
    }
}
