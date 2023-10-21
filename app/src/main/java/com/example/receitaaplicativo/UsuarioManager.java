package com.example.receitaaplicativo;

import java.util.ArrayList;
import java.util.List;

public class UsuarioManager {
    class Autenticacao {
        private List<Usuario> usuarios = new ArrayList<>();

        public void cadastrarUsuario(String nomeDeUsuario, String senha) {
            Usuario usuario = new Usuario(nomeDeUsuario, senha);
            usuarios.add(usuario);
            System.out.println("Usuário cadastrado com sucesso.");
        }

        public boolean autenticar(String nomeDeUsuario, String senha) {
            for (Usuario usuario : usuarios) {
                if (usuario.nomeDeUsuario.equals(nomeDeUsuario) && usuario.senha.equals(senha)) {
                    return true;
                }
            }
            return false;
        }
    }

    public class Usuario {
        public String nomeDeUsuario;
        String senha;

        public Usuario(String nomeDeUsuario, String senha) {
            this.nomeDeUsuario = nomeDeUsuario;
            this.senha = senha;
        }
    }

    private static UsuarioManager instance;
    private List<Usuario> usuarios = new ArrayList<>();

    private UsuarioManager() {
    }

    public static UsuarioManager getInstance() {
        if (instance == null) {
            instance = new UsuarioManager();
        }
        return instance;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void cadastrarUsuario(String nomeDeUsuario, String senha) {
        Usuario usuario = new Usuario(nomeDeUsuario, senha);
        usuarios.add(usuario);
    }

    public boolean autenticar(String nomeDeUsuario, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.nomeDeUsuario.equals(nomeDeUsuario) && usuario.senha.equals(senha)) {
                return true;
            }
        }
        return false;
    }
}

