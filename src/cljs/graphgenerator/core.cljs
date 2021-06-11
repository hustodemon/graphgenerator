(ns graphgenerator.core
  (:require
   [graphgenerator.db :as db]
   [graphgenerator.pages.generator :as gen-pages]
   [day8.re-frame.http-fx]
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [graphgenerator.ajax :as ajax]
   [graphgenerator.events]
   [reitit.core :as reitit]
   [reitit.frontend.easy :as rfe]
   [clojure.string :as string])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
   title])

(defn navbar [] 
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "graphgenerator"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click #(swap! expanded? not)
                  :class (when @expanded? :is-active)}
                 [:span][:span][:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Generator" :gen]
                 [nav-link "#/docs" "Docs" :docs]
                 [nav-link "#/api-docs" "API" :api-docs]
                 [nav-link "#/about" "About" :about]]]]))


(defn about-page []
  [:section.section>div.container>div.content
   "Created by Franky with awesome Clojure tools/libs"])


(defn docs-page []
  [:section.section>div.container>div.content
   (when-let [docs @(rf/subscribe [:docs])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])


(defn api-docs-page []
  [:section.section>div.container>div.content
   (when-let [api @(rf/subscribe [:api-docs])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html api)}}])])


(defn page []
  (when-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))


(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))


(def router
  (reitit/router
   [["/" {:name :gen
          :view #'gen-pages/gen-page}]
    ["/docs" {:name        :docs
              :view        #'docs-page
              :controllers [{:start (fn [_] (rf/dispatch [:fetch-document "/docs" :set-docs]))}]}]
    ["/api-docs" {:name        :api-docs
                  :view        #'api-docs-page
                  :controllers [{:start (fn [_] (rf/dispatch [:fetch-document "/api-docs" :set-api-docs]))}]}]
    ["/about" {:name :about
               :view #'about-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))


(defn init-db! []
  (rf/dispatch [:common/set-db db/initial-db]))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (init-db!)
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
