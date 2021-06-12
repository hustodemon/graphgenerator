(ns graphgenerator.pages.generator
  "
  The UI related to the generator
  "
  (:require
    [day8.re-frame.http-fx]
    [re-frame.core :as rf]
    [re-com.core :as rc]
    [graphgenerator.events]))


(defn- input-area []
  (let [text (rf/subscribe [:generator/input])]
    (fn []
      [rc/input-textarea
       :model text
       :on-change (fn [val]
                    (rf/dispatch [:common/set-value val :generator/input]))
       :width "800px"
       :height "400px"
       :style {:font-family "Consolas, Lucida Console, monospace"}])))


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
  (let [in-progress? (rf/subscribe [:generator/in-progress?])]
    [rc/v-box
     :gap "10px"
     :children
     [[selection]
      [graphviz-tool-selection]
      [presets]
      [rc/button
       :label "Generate!"
       :disabled? @in-progress?
       :on-click #(rf/dispatch [:generate])
       :class "btn-primary"
       :style {:width "200px"}]]]))



(defn output-area []
  (let [graph        (rf/subscribe [:graph])
        in-progress? (rf/subscribe [:generator/in-progress?])]
    (fn []
      [:div
       (if @in-progress?
         [rc/throbber
          :size :large
          :style {:text-align "center"}]
         [:div
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
