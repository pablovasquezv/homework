import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { LoginService } from './login.service';
import Swal from 'sweetalert2';
import { Usuario } from '../models/usuario';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent{

  hide = true;
  usuario: Usuario = new Usuario();
  checked: boolean = false;
  
  constructor(private route: ActivatedRoute,
    private router: Router, private _loginService : LoginService) {
      localStorage.setItem("currentUser", '');
      localStorage.setItem("token", '');
    }



  onSubmit(user, pass) {
    if(!this.checked){
      Swal.fire({
        icon: 'error',
        text: 'Debe de aceptar los Términos y condiciones'
      })
      return;
    }
    this._loginService.login(user, pass).subscribe(
      (response)=>{
        console.log(response);
        this.usuario = response;
        //se envian los datos del usuario al servicio, para compartirlo con otras pantallas
        if (this.usuario.code === '200') {
          this._loginService.setUser(this.usuario);
          //cambiar ruta por /user/profile
          this.router.navigate(['//app/']);
        }else{
          Swal.fire({
            title: '<strong>Error login</strong>',
            icon: 'error',
            html: 'Usuario o password no son válidos',
            showCloseButton: true,
            focusConfirm: true,
            confirmButtonText:'Re-intentar'
          });
        }
      }, 
      (err)=>{
        console.error(err);
        Swal.fire({
          title: '<strong>Error login</strong>',
          icon: 'error',
          html:
            'Ha ocurrido un error',
          showCloseButton: true,
          focusConfirm: true,
          confirmButtonText:'Re-intentar'
        });
      });
  }

  checkAgree(event){
    this.checked = event.checked;
  }

  showAgree(){
    Swal.fire({
      title: '<strong>Términos y condiciones</strong>',
      icon: 'info',
      html:
        '<p align="justify">' +
        'Como parte de las medidas desplegadas por la Compañía para velar por el bienestar de sus colaboradores y' +
        ' evitar la propagación del Covid-19, se ha generado esta herramienta con la finalidad de consolidar y actualizar' +
        ' la información de todo nuestro personal frente a la emergencia. En este acto, el colaborador otorga expresamente' +
        ' su consentimiento a la Compañía para tratar los datos personales que cargue en el aplicativo, así como el hecho' +
        ' de si se encuentra o no contagiado con el virus, y si se encuentra o no en cuarentena.' +
        ' Adicionalmente, el colaborador declara conocer y aceptar de modo expreso que los datos que entregue podrán ser' +
        ' visualizados por el resto de los colaboradores que accedan a la plataforma, debido al grado de desarrollo en que se encuentra la misma.' +
        '</p>',
      showCloseButton: true,
      focusConfirm: true,
      confirmButtonText: 'Aceptar',
      width: '50%'
    })
  }
}
