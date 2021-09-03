(ns graphgenerator.pages.generator
  "
  The UI related to the generator
  "
  (:require
    [graphgenerator.events]
    [day8.re-frame.http-fx]
    [re-frame.core :as rf]
    [re-com.core :as rc]
    [clojure.string :as string]))


(defn- input-area []
  (let [text (rf/subscribe [:generator/input])]
    (fn []
      [:textarea.graph-input-area.rc-input-text-field
       {:value       @text
        :on-change   (fn [evt]
                       (rf/dispatch [:common/set-value
                                     (-> evt .-target .-value)
                                     :generator/input]))
        :on-keyPress (fn [evt]
                       (when (and
                              (.-ctrlKey evt)
                              (= 0 (.-keyCode evt)))
                         (rf/dispatch [:generator/generate])))}])))


(defn- presets []
  (let [graph-type      (rf/subscribe [:generator/selected-graph-type])
        presets         (rf/subscribe [:generator/presets])
        selected-preset (rf/subscribe [:generator/selected-preset])]
    (fn []
      (let [choices (cons {:id 0 :label "<nothing>" :text "nothing"}
                          (get @presets @graph-type))]
        [:div
         [rc/title
          :label "Select preset (optional)"
          :level :level4]
         [rc/single-dropdown
          :choices   choices
          :model     (second @selected-preset)
          :width     "200px"
          :on-change (fn [val]
                       (rf/dispatch
                        [:generator/select-preset
                         [@graph-type val]]))]]))))


(defn- selection []
  (let [graph-types (rf/subscribe [:generator/graph-types])
        graph-type (rf/subscribe [:generator/selected-graph-type])]
    (fn []
      [:div
       [rc/title
        :label "Select graph type"
        :level :level4]
       [rc/single-dropdown
        :choices @graph-types
        :model graph-type
        :width "200px"
        :on-change (fn [val] ;; todo a single dispatch
                     (rf/dispatch [:common/set-value
                                   [val 0]
                                   :generator/selected-preset])
                     (rf/dispatch [:common/set-value
                                   val
                                   :generator/selected-graph-type]))]])))

;; todo description of the UI elements
(defn- graphviz-tool-selection []
  (let [graphviz-types (rf/subscribe [:generator/graphviz-types])
        graphviz-type  (rf/subscribe [:generator/selected-graphviz-type])
        graph-type     (rf/subscribe [:generator/selected-graph-type])]
    (fn []
      (when (= @graph-type :graphviz)
        [:div
         [rc/title
          :label "Select graphviz program"
          :level :level4]
         [rc/single-dropdown
          :choices @graphviz-types
          :model graphviz-type
          :width "200px"
          :on-change (fn [val] ;; todo a single dispatch
                       (rf/dispatch [:common/set-value
                                     val
                                     :generator/selected-graphviz-type]))]]))))


(defn params-area []
  (let [in-progress? (rf/subscribe [:generator/in-progress?])
        graph        (rf/subscribe [:generator/graph])
        error        (rf/subscribe [:generator/error])]
    [rc/v-box
     :gap "10px"
     :children
     [[selection]
      [graphviz-tool-selection]
      [presets]
      [rc/button
       :label "Generate (Ctrl+Enter)"
       :disabled? @in-progress?
       :on-click #(rf/dispatch [:generator/generate])
       :class "btn-primary"
       :style {:width "200px"}]
      [rc/button
       :label "Download"
       :disabled? (or (string/blank? @graph) (some? @error) @in-progress?)
       :on-click #(rf/dispatch [:generator/invoke-graph-download])
       :class "btn-primary"
       :style {:width "200px"}]]]))


(defn output-area []
  (let [graph        (rf/subscribe [:generator/graph])
        in-progress? (rf/subscribe [:generator/in-progress?])
        error        (rf/subscribe [:generator/error])]
    (fn []
      [:div
       (cond
         (some? @error) [rc/alert-box
                         :alert-type :danger
                         :body @error]
         @in-progress?  [rc/throbber
                         :size :large
                         :style {:text-align "center"}]
         :else          [:div
                         {:dangerouslySetInnerHTML {:__html @graph}
                          :style                   {:text-align "center"}}])])))


(defn gen-page []
  [:section.section>div.container>div.content
   [rc/v-box
    :gap "20px"
    :align :center
    :children
    [[rc/h-box
      :gap "10px"
      :align :center
      :children
      [[input-area]
       [params-area]]]
     [output-area]]]])
