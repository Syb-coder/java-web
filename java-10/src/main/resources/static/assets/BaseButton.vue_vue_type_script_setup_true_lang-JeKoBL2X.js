import{c as o}from"./createLucideIcon-BuKOVmEw.js";import{c as m}from"./utils-DaT-yT0k.js";import{d as u,c as p,n as y,l as f,e as b,y as k,D as g,p as h,k as s}from"./index-DHGk6Q2d.js";/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const x=o("loader-circle",[["path",{d:"M21 12a9 9 0 1 1-6.219-8.56",key:"13zald"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const j=o("search",[["path",{d:"m21 21-4.34-4.34",key:"14j7rj"}],["circle",{cx:"11",cy:"11",r:"8",key:"4ej97u"}]]),v=["type","disabled"],L=u({__name:"BaseButton",props:{type:{default:"button"},variant:{default:"primary"},size:{default:"md"},disabled:{type:Boolean,default:!1},loading:{type:Boolean,default:!1}},emits:["click"],setup(e,{emit:n}){const t=e,r=n,i={primary:"bg-primary-600 text-white hover:bg-primary-700",secondary:"bg-white border border-ink-200 text-ink-700 hover:bg-ink-50",danger:"bg-red-600 text-white hover:bg-red-700",ghost:"text-ink-600 hover:bg-ink-100"},c={sm:"px-3 py-1.5 text-sm",md:"px-4 py-2",lg:"px-5 py-2.5 text-base"},l=h(()=>m("inline-flex items-center justify-center rounded-md font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500/30 disabled:opacity-50 disabled:cursor-not-allowed",i[t.variant],c[t.size])),d=a=>{t.disabled||t.loading||r("click",a)};return(a,C)=>(s(),p("button",{type:e.type,class:y(l.value),disabled:e.disabled||e.loading,onClick:d},[e.loading?(s(),f(b(x),{key:0,class:"mr-1.5 h-4 w-4 animate-spin"})):k("",!0),g(a.$slots,"default")],10,v))}});export{x as L,j as S,L as _};
