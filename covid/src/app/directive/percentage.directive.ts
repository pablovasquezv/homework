import { Directive, HostListener, Input } from '@angular/core';
import { Inject, LOCALE_ID } from '@angular/core';
import { NgModel, ControlValueAccessor } from '@angular/forms';

@Directive({
  selector: '[appPercentage]'
})
export class PercentageDirective implements ControlValueAccessor  {
  @Input("format") percentOptions:string;
  onChange;
  onTouched;

  constructor( @Inject(LOCALE_ID) public locale: string,
               public model:NgModel) {
   
  }
  
  ngAfterContentInit(event : any){
    Promise.resolve(event).then(() => {
     this.format(this.model.viewModel);
    });
}

  registerOnChange( fn : any ) : void {
    this.onChange = fn;
  }

  writeValue(fn: any): void {
    throw new Error("Method not implemented.");
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  setDisabledState?(isDisabled: boolean): void {
    throw new Error("Method not implemented.");
  }
  @HostListener('focus', [ '$event.target.value' ]) onfocus( value ) {
    if(value &&(value as string).indexOf('%')!=-1){
      value=(value as string).replace('%','').trim();
    }
    this.model.valueAccessor.writeValue(value);
  }
  /*formatValue(value:number): string{
    const numValue=value/100;
    const visualValue=formatPercent(numValue,this.locale,this.percentOptions);
    console.log("blur: "+value+"-"+visualValue+"-"+numValue);
    return visualValue;
  }*/

  format(value){
    if(value){
      if (!isNaN(value)) {
    this.model.valueAccessor.writeValue(value+" %");
    
    }
    else
    this.model.valueAccessor.writeValue("");
    }
  }
  @HostListener('blur', [ '$event.target.value' ]) onblur( value ) {
    this.format(value);
  }
}
