package edu.mtisw.KartingRM.controllers;

import edu.mtisw.KartingRM.entities.ClientEntity;
import edu.mtisw.KartingRM.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin("*")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientEntity> createClient(@RequestBody ClientEntity client) {
        ClientEntity createdClient = clientService.createClient(client);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClientEntity>> getAllClients() {
        List<ClientEntity> clients = clientService.getAllClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientEntity> getClientById(@PathVariable Long id) {
        Optional<ClientEntity> clientOpt = clientService.getClientById(id);
        if (clientOpt.isPresent()) {
            return new ResponseEntity<>(clientOpt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientEntity> updateClient(@PathVariable Long id, @RequestBody ClientEntity clientDetails) {
        Optional<ClientEntity> clientOpt = clientService.getClientById(id);
        if (clientOpt.isPresent()) {
            ClientEntity existingClient = clientOpt.get();
            existingClient.setName(clientDetails.getName());
            ClientEntity updatedClient = clientService.updateClient(existingClient);
            return new ResponseEntity<>(updatedClient, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id); // Delete the client
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/discount")
    public ResponseEntity<Double> getClientDiscount(@PathVariable Long id) {
        double discount = clientService.calculateDiscountForClient(id);
        return new ResponseEntity<>(discount, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ClientEntity> login(@RequestBody Map<String,String> creds) {
        String email    = creds.get("email");
        String password = creds.get("password");
        return clientService.login(email, password)
                .map(c -> ResponseEntity.ok(c))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"
                ));
    }

}
