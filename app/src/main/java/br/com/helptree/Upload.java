package br.com.helptree;

public class Upload {

    private String mImgName;
    private String mImgUrl;

    public Upload(){


    }

    public Upload(String name, String imgUrl ){
        if(name.trim().equals("")){
            name = "sem nome";
        }

        mImgName = name;
        mImgUrl = imgUrl;

    }

    public String getName(){
        return mImgName;
    }

    public void setNome(String nome){
        mImgName = nome;
    }

    public String getmImgUrl(){
        return mImgUrl;
    }

    public void setmImgUrl(String imgUrl){
        mImgUrl = imgUrl;
    }


}
