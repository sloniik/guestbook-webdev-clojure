(ns guestbook.routes.home
  (:require [compojure.core :refer :all]
            [guestbook.models.db :as db]
            [guestbook.views.layout :as layout]
            [noir.session :as session]
            [hiccup.form :refer :all :as form]
            [hiccup.core :refer :all]
            [hiccup.element :refer :all]
            [hiccup.def :refer :all]))

(defn show-guests
  []
  [:ul.guest
   (for [{:keys [message name timestamp]}
         (db/read-guests)]
     [:li
      [:blockquote message]
      [:p "-" [:cite name]]
      [:time timestamp]])])

(defn home
  [& [name message error]]
  (layout/common
    [:h1 "Guestbook: " (session/get :user)]
    [:p "Welcome to my guestbook"]
    [:p error]
    (show-guests)
    [:hr]
    (form/form-to [:post "/"]
             [:p "Name:"]
             (form/text-field "name" name)
             [:p "Message:"]
             (form/text-area {:rows 10 :cols 40} "message" message)
             [:br]
             (form/submit-button "comment"))))

(defn save-message
  [name message]
  (cond
    (empty? name)
    (home name message "Some dummy forgot to leave a name")
    (empty? message)
    (home name message "Don't you have something to say?")
    :else
    (do
      (db/save-message name message)
      (home))))

(defroutes home-routes
           (GET "/" [] (home))
           (POST "/" [name message]
             (save-message name message)))

;(html
;  [:div
;   {:id "hello",
;    :class "content"}
;   [:p "hello world"]])
;
;(html
;  [:div#hello.content
;   [:p "Hello world!"]])
;
;(html
;  (link-to {:aligh "left"}
;         "http://google.com" "google"))
;(html
;  (form/form-to [:post "/"]
;              [:p "Name:"]
;              (form/text-field "name")
;              [:p "Message:"]
;              (form/text-area {:rows 10 :cols 40} "message")
;              [:br]
;              (form/submit-button "comment")))
;
;(defhtml page [& body]
;         [:html
;          [:head
;           [:title "Welcome!"]
;           [:body body]]])
;
;(page "<h1> hello </h1>")
;(image "/img/test.jpg" "alt text")