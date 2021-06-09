(ns graphgenerator.routes.home
  (:require
   [graphgenerator.generator.core :as generator]
   [graphgenerator.layout :as layout]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [graphgenerator.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))


(defn home-page [request]
  (layout/render request "home.html"))


(defn docs [_]
  (fn [_]
    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
        (response/header "Content-Type" "text/plain; charset=utf-8"))))


(defn generate-dot [req]
  (try
    (response/ok
     (generator/generate-graph-dot
      (:body-params req)))
    (catch Throwable e
      (response/internal-server-error (-> e .getData :msg)))))


(defn generate [req]
  (try
    (response/ok
     (generator/generate-graph-rhizome
      (edn/read-string
       (:body-params req))
      ));; todo catch only exception-info
    (catch Throwable e
      ;; todo ISE - something else maybe
      (response/internal-server-error (str e)))))


(defn home-routes []
  [""
   {:middleware [middleware/wrap-formats]} ;; todo csrf when needed
   ["/" {:get home-page}]
   ["/generate-dot"
    {:post generate-dot}]
   ["/generate"
    {:post generate}]
   ["/docs"
    {:get docs}]])

