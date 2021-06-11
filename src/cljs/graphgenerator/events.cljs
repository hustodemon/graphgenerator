(ns graphgenerator.events
  "
  Re-frame events and subscriptions
  "
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))


(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))


(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))


(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))


(rf/reg-event-db
  :common/set-value
  (fn [db [_ val & path]]
    ;; not sure if this works well for nested stuff
    (assoc-in db path val)))


(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))


(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))


(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch [:fetch-docs]}))


(rf/reg-event-db
 :common/set-db
 (fn [_ [_ new-db]]
   new-db))


(rf/reg-event-db
  :generator/select-preset
  (fn [db [_ [preset-graph-type preset-id]]]
    ;; not sure if this works well for nested stuff
    (let [presets-of-type (get-in db [:generator/presets preset-graph-type])
          preset          (first (filter #(= preset-id (:id %)) presets-of-type))]
    (prn "selecting preset" (cons :generator/presets preset))
      (-> db
          (assoc :generator/selected-preset [preset-graph-type preset-id])
          (assoc :generator/input (:text preset))
          ))))


(rf/reg-event-db
 :set-graph
 (fn [db [_ graph]]
   (.log js/console graph)
   (-> db
       (assoc :graph graph)
       (assoc :generator/in-progress? false))))


(rf/reg-event-db
 :set-error
 (fn [db [_ err]]
   (js/alert (:response err))
   (assoc db :generator/in-progress? false)))


(rf/reg-event-fx
 :generate
 (fn [cofx [_ _]]
   (let [db               (:db cofx)
         selected-type    (:generator/selected-graph-type db)
         graphviz-program (:generator/selected-graphviz-type db)]
     {:http-xhrio {:method          :post
                   :uri             (str "/generate-graph"
                                         "?type=" (name selected-type)
                                         "&program=" (name graphviz-program))
                   :params          (get-in cofx [:db :generator/input])
                   :format          (ajax/text-request-format)
                   :response-format (ajax/raw-response-format)
                   :headers         {"Accept" "image/svg+xml"}
                   :on-success      [:set-graph]
                   :on-failure      [:set-error]}
      :db         (assoc db :generator/in-progress? true)})))


;;subscriptions

(rf/reg-sub
  :common/route
  (fn [db _]
    (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))

(rf/reg-sub ;;; todo maybe scratch
  :graph
  (fn [db _]
    (:graph db)))

(rf/reg-sub
  :generator/input
  (fn [db _]
    (:generator/input db)))

(rf/reg-sub
  :generator/graph-types
  (fn [db _]
    (:generator/graph-types db)))

(rf/reg-sub
  :generator/graphviz-types
  (fn [db _]
    (:generator/graphviz-types db)))

(rf/reg-sub
  :generator/selected-graph-type
  (fn [db _]
    (:generator/selected-graph-type db)))

(rf/reg-sub
 :generator/selected-graphviz-type
  (fn [db _]
    (:generator/selected-graphviz-type db)))

(rf/reg-sub
 :generator/presets
 (fn [db _]
   (get-in db [:generator/presets])))

(rf/reg-sub
 :generator/selected-preset
  (fn [db _]
    (:generator/selected-preset db)))

(rf/reg-sub
 :generator/in-progress?
  (fn [db _]
    (:generator/in-progress? db)))
