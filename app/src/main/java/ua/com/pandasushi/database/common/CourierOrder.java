package ua.com.pandasushi.database.common;

import java.io.Serializable;
import java.util.Date;

public class CourierOrder implements Serializable {


    //Блок зі статичними даними, програма їх змінювати не буде
    private Long orderID; //id замовлення
    private String charcode; //номер замовлення
    private String phone; // телефон клієнта
    private String name; // ім'я клієнта
    private String street;
    private String house;
    private String apartament;
    /*
    private Integer region; // ID району, залежить фон замовлення та скорочення
    замість цього - наступні 2 поля
    */
    private String regionCharcode;
    private Integer regionBackground;
    private Date preferedTime; //на цей час мають орієнтуватись кур’єри
    private Date promiseTime; //обіцяний час, після нього йде компенсація
    private Integer finalCost; //сума до оплати
    private String dishes; // строка виду «РПНС», кожна буква відповідає що має бути в замовленні, відображатись буде значками
    //Кінець блоку зі статичними даними

    //Оновлюється в момент коли кур'єр бере або відміняє замовлення
    private Integer courierId; //тут id кур'єра
    private Date sendTime; //тут час виїзду
    //Кінець

    //Оновлюється при доставці в локальній БД, потім записується на сервер
    private Date deliverTime; // тут фактичний час доставки
    private Points point; // точка в якій був кур'єр коли натиснув кнопку доставлено
    //Кінець


    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public String getCharcode() {
        return charcode;
    }

    public void setCharcode(String charcode) {
        this.charcode = charcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getApartament() {
        return apartament;
    }

    public void setApartament(String apartament) {
        this.apartament = apartament;
    }

    public String getRegionCharcode() {
        return regionCharcode;
    }

    public void setRegionCharcode(String regionCharcode) {
        this.regionCharcode = regionCharcode;
    }

    public Integer getRegionBackground() {
        return regionBackground;
    }

    public void setRegionBackground(Integer regionBackground) {
        this.regionBackground = regionBackground;
    }

    public Date getPreferedTime() {
        return preferedTime;
    }

    public void setPreferedTime(Date preferedTime) {
        this.preferedTime = preferedTime;
    }

    public Date getPromiseTime() {
        return promiseTime;
    }

    public void setPromiseTime(Date promiseTime) {
        this.promiseTime = promiseTime;
    }

    public Integer getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(Integer finalCost) {
        this.finalCost = finalCost;
    }

    public String getDishes() {
        return dishes;
    }

    public void setDishes(String dishes) {
        this.dishes = dishes;
    }

    public Integer getCourierId() {
        return courierId;
    }

    public void setCourierId(Integer courierId) {
        this.courierId = courierId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Date deliverTime) {
        this.deliverTime = deliverTime;
    }

    public Points getPoint() {
        return point;
    }

    public void setPoint(Points point) {
        this.point = point;
    }
}
