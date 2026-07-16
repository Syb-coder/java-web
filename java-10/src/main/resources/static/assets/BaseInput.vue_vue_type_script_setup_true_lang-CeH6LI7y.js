import{c as k}from"./utils-DaT-yT0k.js";import{d as f,c as n,a as s,n as x,l as v,m as b,y as d,t as w,p as i,k as o}from"./index-DHGk6Q2d.js";import{S as g}from"./BaseButton.vue_vue_type_script_setup_true_lang-JeKoBL2X.js";import{C as M}from"./calendar-CaUDjPJ4.js";import{c as a}from"./createLucideIcon-BuKOVmEw.js";/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const C=a("eye-off",[["path",{d:"M10.733 5.076a10.744 10.744 0 0 1 11.205 6.575 1 1 0 0 1 0 .696 10.747 10.747 0 0 1-1.444 2.49",key:"ct8e1f"}],["path",{d:"M14.084 14.158a3 3 0 0 1-4.242-4.242",key:"151rxh"}],["path",{d:"M17.479 17.499a10.75 10.75 0 0 1-15.417-5.151 1 1 0 0 1 0-.696 10.75 10.75 0 0 1 4.446-5.143",key:"13bj9a"}],["path",{d:"m2 2 20 20",key:"1ooewy"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const V=a("eye",[["path",{d:"M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0",key:"1nclc0"}],["circle",{cx:"12",cy:"12",r:"3",key:"1v7zrd"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const B=a("key",[["path",{d:"m15.5 7.5 2.3 2.3a1 1 0 0 0 1.4 0l2.1-2.1a1 1 0 0 0 0-1.4L19 4",key:"g0fldk"}],["path",{d:"m21 2-9.6 9.6",key:"1j0ho8"}],["circle",{cx:"7.5",cy:"15.5",r:"5.5",key:"yqb3hr"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const I=a("lock",[["rect",{width:"18",height:"11",x:"3",y:"11",rx:"2",ry:"2",key:"1w4ew1"}],["path",{d:"M7 11V7a5 5 0 0 1 10 0v4",key:"fwvmzm"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const z=a("mail",[["path",{d:"m22 7-8.991 5.727a2 2 0 0 1-2.009 0L2 7",key:"132q7q"}],["rect",{x:"2",y:"4",width:"20",height:"16",rx:"2",key:"izxlao"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const L=a("phone",[["path",{d:"M13.832 16.568a1 1 0 0 0 1.213-.303l.355-.465A2 2 0 0 1 17 15h3a2 2 0 0 1 2 2v3a2 2 0 0 1-2 2A18 18 0 0 1 2 4a2 2 0 0 1 2-2h3a2 2 0 0 1 2 2v3a2 2 0 0 1-.8 1.6l-.468.351a1 1 0 0 0-.292 1.233 14 14 0 0 0 6.392 6.384",key:"9njp5v"}]]);/**
 * @license lucide-vue-next v0.511.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const j=a("user",[["path",{d:"M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2",key:"975kel"}],["circle",{cx:"12",cy:"7",r:"4",key:"17ys0d"}]]),q=["type","value","placeholder","disabled"],E={key:0,class:"mt-1 text-xs text-red-500"},K=f({__name:"BaseInput",props:{modelValue:{default:""},type:{default:"text"},placeholder:{default:""},icon:{},disabled:{type:Boolean,default:!1},error:{}},emits:["update:modelValue"],setup(e,{emit:y}){const t=e,p=y,m={User:j,Lock:I,Phone:L,Key:B,Mail:z,Eye:V,EyeOff:C,Calendar:M,Search:g},r=i(()=>t.icon?m[t.icon]:void 0),h=i(()=>k("flex items-center rounded-md border bg-white transition-colors focus-within:ring-2 focus-within:ring-primary-500/30",t.error?"border-red-500":"border-ink-200",t.disabled&&"cursor-not-allowed bg-ink-50 opacity-50")),u=c=>{const l=c.target;p("update:modelValue",l.value)};return(c,l)=>(o(),n("div",null,[s("div",{class:x(h.value)},[r.value?(o(),v(b(r.value),{key:0,class:"ml-3 h-4 w-4 shrink-0 text-ink-400"})):d("",!0),s("input",{type:e.type,value:e.modelValue,placeholder:e.placeholder,disabled:e.disabled,class:"w-full bg-transparent px-3 py-2 text-sm text-ink-800 placeholder-ink-400 focus:outline-none",onInput:u},null,40,q)],2),e.error?(o(),n("p",E,w(e.error),1)):d("",!0)]))}});export{K as _};
