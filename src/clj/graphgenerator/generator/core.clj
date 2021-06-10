(ns graphgenerator.generator.core
  "
  Generate graphs using various tools
  "
  (:require
   [rhizome.viz :as viz]
   [clojure.edn :as edn]
   [clojure.java.shell :as shell]))


(defmulti generate-graph
  "Multimethod for generating graphs"
  (fn [graph] (:type graph)))


;;
;; "Generate graph using Graphviz Dot. The parameter src is a source of
;; the Dot data (stream, string, ...).
;;
;; Throw an ex-info with :msg if something goes wrong."
(def valid-commands #{"dot" "neato" "twopi" "circo" "fdp"})

(defmethod generate-graph :dot [{:keys [src tool]}]
  (when-not (contains? valid-commands tool)
    (throw (ex-info "Invalid command" {:data tool})))
  (let [command                (str "/usr/bin/" tool)
        {:keys [exit out err]} (shell/sh command "-Tsvg" :in src)]
    (if (= 0 exit)
      out
      (throw (ex-info "Graphviz error" {:msg err})))))


;; "Generate graph using Rhizome tool."
(defmethod generate-graph :rhizome [{:keys [src]}]
  ;; src "contains" edn as string
  (when (instance? java.lang.String src)
    (throw (ex-info "We do not welcome strings here" {})))
  (let [parsed (-> src slurp edn/read-string)]
    (viz/graph->svg
     (keys parsed)
     parsed
     :node->descriptor (fn [n] {:label n}))))
