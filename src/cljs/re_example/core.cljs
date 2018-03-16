(ns re-example.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views

(defonce counter (atom 0))

(defn hello-component [name]
  [:div
   [:p (str "Hello " name)]])

(defn lister [items]
  [:ul
   (for [item items]
     ^{:key item} [:li "Item " item])])
 
(defn lister-user [num]
  [:div
   "Here is a list:"
   [lister (range num)]])

(defn timer []
  (let [elapsed (atom 0)]
    (fn []
      (js/setTimeout #(swap! elapsed inc) 1000)
      [:div
        "Seconds elapsed: " @elapsed])))

(defn input-state [text-atom]
  [:input {:type "text"
           :value @text-atom
           :on-change #(reset! text-atom (-> % .-target .-value))}])
  

(defn shared-state []
  (let [state (atom "default value")]
    (fn []
      [:div
        [:p "Current state value: " @state]
        [:p [input-state state]]])))
  
(defn counting-component []
  [:div
    "The atom value : "
    @counter ". "
    [:input {:type "button" :value "+1" :on-click #(swap! counter inc)}]
    [:input {:type "button" :value "Reset" :on-click #(reset! counter 0)}]])

(defn home-page []
  [:div 
   [:h2 "Welcome to Re-example"]
   [counting-component]
   [timer]
   [shared-state]
   [:p 
    [:strong "BOLD"] "_and " 
    [:span {:style {:color "red"}} "red"]]
   [:div [:a {:href "/about"} "go to about page"]]
   [:div [:a {:href "/todo"} "go to todo page"]]])



(defn about-page []
  [:div [:h2 "About re-example"]
   [:div [:a {:href "/"} "go to the home page"]]])


;; -------------------------
;; Routes

(defonce page (atom #'home-page))


(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

;; Todo components

(defonce todo-state (atom {:tasks ["lol", "kek", "cheburek"]}))

(defn task-table [tasks]
  [:table 
   [:th]
   [:th "descriptions"]
   (for [task (:tasks @tasks)]
    [:tr
     [:td "-"]
     [:td task]])])

(defn add-task [desc]
  (swap! todo-state update-in [:tasks] (fn [t] (conj t desc))))
(defn todo-page []
  [:div [:h2 "Todo Reagent example"]
   [task-table todo-state]
   [:div [:a {:href "/"} "got to home page"]]])

(secretary/defroute "/todo" []
  (reset! page #'todo-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (add-task ["NONON"])
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
