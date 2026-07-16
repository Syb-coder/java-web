import{c as m}from"./createLucideIcon-BuKOVmEw.js";import{d as v,z as V,B as $,l as B,b as y,T as M,g as z,C as S,k as l,c as d,a as o,t as k,e as h,D as x,y as g,E as L,n as b,_ as E,F as w,r as T,p as _,G as P}from"./index-DHGk6Q2d.js";import{c as C}from"./utils-DaT-yT0k.js";import{S as I}from"./BaseButton.vue_vue_type_script_setup_true_lang-JeKoBL2X.js";/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const j=m("chevron-left",[["path",{d:"m15 18-6-6 6-6",key:"1wnfg3"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const K=m("chevron-right",[["path",{d:"m9 18 6-6-6-6",key:"mthhwq"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const oe=m("pencil",[["path",{d:"M21.174 6.812a1 1 0 0 0-3.986-3.987L3.842 16.174a2 2 0 0 0-.5.83l-1.321 4.352a.5.5 0 0 0 .623.622l4.353-1.32a2 2 0 0 0 .83-.497z",key:"1a8usu"}],["path",{d:"m15 5 4 4",key:"1mk7zo"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const se=m("plus",[["path",{d:"M5 12h14",key:"1ays0h"}],["path",{d:"M12 5v14",key:"s699le"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const ne=m("trash-2",[["path",{d:"M3 6h18",key:"d0wm0j"}],["path",{d:"M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6",key:"4alrt4"}],["path",{d:"M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2",key:"v07s0e"}],["line",{x1:"10",x2:"10",y1:"11",y2:"17",key:"1uufr5"}],["line",{x1:"14",x2:"14",y1:"11",y2:"17",key:"xtxkd"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const N=m("x",[["path",{d:"M18 6 6 18",key:"1bl5f8"}],["path",{d:"m6 6 12 12",key:"d8bk6v"}]]),D={key:0,class:"fixed inset-0 z-50 flex items-center justify-center p-4"},F={class:"flex items-center justify-between border-b border-ink-200 px-5 py-4"},q={class:"text-base font-semibold text-ink-800"},A={class:"max-h-[70vh] overflow-y-auto px-5 py-4"},G={key:0,class:"border-t border-ink-200 px-5 py-3"},H=v({__name:"BaseModal",props:{modelValue:{type:Boolean,default:!1},title:{default:""},width:{default:"500px"}},emits:["update:modelValue","close"],setup(t,{emit:p}){const s=t,u=p,n=()=>{u("update:modelValue",!1),u("close")},i=e=>{e.key==="Escape"&&s.modelValue&&n()};V(()=>s.modelValue,e=>{e?(document.addEventListener("keydown",i),document.body.style.overflow="hidden"):(document.removeEventListener("keydown",i),document.body.style.overflow="")}),$(()=>{document.removeEventListener("keydown",i),document.body.style.overflow=""});const r=C("relative z-10 overflow-hidden rounded-lg bg-white shadow-xl");return(e,a)=>(l(),B(S,{to:"body"},[y(M,{name:"modal"},{default:z(()=>[t.modelValue?(l(),d("div",D,[o("div",{class:"modal-overlay absolute inset-0 bg-black/40",onClick:n}),o("div",{class:b([h(r),"modal-panel"]),style:L({width:t.width})},[o("div",F,[o("h3",q,k(t.title),1),o("button",{class:"rounded p-1 text-ink-400 transition-colors hover:bg-ink-100 hover:text-ink-600",onClick:n},[y(h(N),{class:"h-5 w-5"})])]),o("div",A,[x(e.$slots,"default",{},void 0,!0)]),e.$slots.footer?(l(),d("div",G,[x(e.$slots,"footer",{},void 0,!0)])):g("",!0)],6)])):g("",!0)]),_:3})]))}}),le=E(H,[["__scopeId","data-v-063461ae"]]),R={class:"flex items-center gap-1 text-sm text-ink-600"},U={class:"mr-2"},X=["disabled"],J={key:0,class:"px-2 text-ink-400"},O=["onClick"],Q=["disabled"],re=v({__name:"BasePagination",props:{total:{},page:{default:1},pageSize:{default:10}},emits:["update:page"],setup(t,{emit:p}){const s=t,u=p,n=_(()=>Math.max(1,Math.ceil(s.total/s.pageSize))),i=_(()=>{const e=n.value,a=s.page;return e<=5?Array.from({length:e},(c,f)=>f+1):a<=3?[1,2,3,4,5,"...",e]:a>=e-2?[1,"...",e-4,e-3,e-2,e-1,e]:[1,"...",a-1,a,a+1,"...",e]}),r=e=>{typeof e=="number"&&(e<1||e>n.value||e===s.page||u("update:page",e))};return(e,a)=>(l(),d("div",R,[o("span",U,"共 "+k(t.total)+" 条",1),o("button",{class:"rounded px-2 py-1 transition-colors hover:bg-ink-100 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-transparent",disabled:t.page<=1,onClick:a[0]||(a[0]=c=>r(t.page-1))},[y(h(j),{class:"h-4 w-4"})],8,X),(l(!0),d(w,null,T(i.value,(c,f)=>(l(),d(w,{key:f},[c==="..."?(l(),d("span",J,"...")):(l(),d("button",{key:1,class:b(["min-w-[32px] rounded px-2 py-1 transition-colors",c===t.page?"bg-primary-600 text-white":"hover:bg-ink-100"]),onClick:Y=>r(c)},k(c),11,O))],64))),128)),o("button",{class:"rounded px-2 py-1 transition-colors hover:bg-ink-100 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-transparent",disabled:t.page>=n.value,onClick:a[1]||(a[1]=c=>r(t.page+1))},[y(h(K),{class:"h-4 w-4"})],8,Q)]))}}),W=["value","placeholder"],de=v({__name:"SearchBar",props:{modelValue:{default:""},placeholder:{default:"请输入关键字搜索"}},emits:["update:modelValue","search"],setup(t,{emit:p}){const s=p,u=r=>{const e=r.target;s("update:modelValue",e.value)},n=()=>{s("search")},i=C("flex items-center rounded-md border border-ink-200 bg-white px-3 py-2 transition-colors focus-within:ring-2 focus-within:ring-primary-500/30");return(r,e)=>(l(),d("div",{class:b(h(i))},[o("button",{type:"button",class:"mr-2 flex shrink-0 items-center text-ink-400 transition-colors hover:text-primary-600",onClick:n},[y(h(I),{class:"h-4 w-4"})]),o("input",{value:t.modelValue,placeholder:t.placeholder,class:"w-full bg-transparent text-sm text-ink-800 placeholder-ink-400 focus:outline-none",onInput:u,onKeyup:P(n,["enter"])},null,40,W)],2))}});export{le as B,se as P,ne as T,de as _,re as a,oe as b};
