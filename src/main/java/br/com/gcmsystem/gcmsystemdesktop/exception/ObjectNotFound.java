package br.com.gcmsystem.gcmsystemdesktop.exception;

public class ObjectNotFound extends RuntimeException{
        private static final long serialVersionUID = 1L;

    public ObjectNotFound(Integer id){
        super("Registro não encontrado com o id: "+id);
    }
    public ObjectNotFound(){
        super("Registro não encontrado");
    }
}
